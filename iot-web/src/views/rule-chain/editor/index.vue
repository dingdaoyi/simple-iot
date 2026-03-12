<script setup>
import { Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NodeConfigPanel from './NodeConfigPanel.vue'
import {
  deviceListApi,
  loadTslData,
  messageReceiveListApi,
  productListApi,
  productTypeListApi,
  pushConfigListApi,
  ruleChainAddApi,
  ruleChainDetailApi,
  ruleChainNodeTypesApi,
  ruleChainUpdateApi,
} from '@/api/index.js'

const route = useRoute()
const router = useRouter()

// 规则链数据
const ruleChain = ref({
  id: null,
  name: '',
  description: '',
  sourceType: 'PRODUCT',
  sourceId: null,
  // 巻加级联选择辅助字段（不保存到后端)
  productTypeId: null,
  productId: null,
  isRoot: false,
  isEnabled: true,
  configuration: {
    nodes: [],
    connections: [],
  },
})

// 节点类型
const nodeTypes = ref([])

// 级联选择数据
const productTypeOptions = ref([])
const productOptions = ref([])
const deviceOptions = ref([])

// 消息接收配置选项（用于消息推送节点）
const messageReceiveOptions = ref([])

// 推送配置选项（用于HTTP/MQTT节点）
const pushConfigOptions = ref([])

// 物模型属性列表（用于属性过滤节点）
const tslProperties = ref([])

// 物模型事件列表（用于事件输入节点）
const tslEvents = ref([])

// 选中的节点
const selectedNode = ref(null)

// 保存加载状态
const saving = ref(false)

// 画布引用
const canvasRef = ref(null)
const svgRef = ref(null)

// 拖拽状态
const dragState = ref({
  isDragging: false,
  dragNode: null,
  dragType: null, // 'new' | 'move' | 'connection'
  startX: 0,
  startY: 0,
  offsetX: 0,
  offsetY: 0,
})

// 连线拖拽状态
const connectionDrag = ref({
  isDragging: false,
  sourceNode: null,
  sourcePort: null,
  tempLine: null,
})
// 临时连线（拖拽中)
const tempConnection = ref(null)

// 节点创建菜单
const nodeCreateMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  sourceNode: null,
  connectionType: null,
  availableTypes: [],
})

// 是否是编辑模式
const isEdit = computed(() => !!route.params.id && route.params.id !== 'new')

// 获取选中节点的上游输入节点（用于过滤节点获取可选属性）
const upstreamInputNode = computed(() => {
  if (!selectedNode.value)
    return null

  // 查找连接到当前节点的上游节点
  const incomingConnections = ruleChain.value.configuration.connections.filter(
    c => c.target === selectedNode.value.id,
  )

  if (incomingConnections.length === 0)
    return null

  // 获取第一个上游节点
  const sourceNodeId = incomingConnections[0].source
  return ruleChain.value.configuration.nodes.find(n => n.id === sourceNodeId)
})

// 判断是否是输入节点
function isInputNode(type) {
  const inputTypes = ['INPUT_PROPERTY', 'INPUT_EVENT', 'INPUT_ONLINE']
  return inputTypes.includes(type)
}

// 判断是否是过滤节点
function isFilterNode(type) {
  const filterTypes = ['FILTER_PROPERTY', 'FILTER_EVENT_TYPE', 'FILTER_SCRIPT']
  return filterTypes.includes(type)
}

// 获取节点分类
function getNodeCategory(type) {
  if (type.startsWith('INPUT_'))
    return 'INPUT'
  if (type.startsWith('FILTER_'))
    return 'FILTER'
  if (type.startsWith('ALARM_'))
    return 'ALARM'
  if (type.startsWith('OUTPUT_'))
    return 'OUTPUT'
  return 'UNKNOWN'
}

// 验证连接是否有效
function isValidConnection(sourceType, targetType, _connectionType) {
  const sourceCategory = getNodeCategory(sourceType)
  const targetCategory = getNodeCategory(targetType)

  // INPUT 节点只能作为源，不能作为目标
  if (targetCategory === 'INPUT') {
    return false
  }

  // OUTPUT 节点是终端节点，不能有后续连接
  if (sourceCategory === 'OUTPUT') {
    return false
  }

  // 输入节点的特殊连接规则
  if (sourceCategory === 'INPUT') {
    // INPUT_PROPERTY (属性上报) → 不能连接 FILTER_EVENT_TYPE
    if (sourceType === 'INPUT_PROPERTY' && targetType === 'FILTER_EVENT_TYPE') {
      return false
    }
    // INPUT_EVENT (事件上报) → 不能连接 FILTER_PROPERTY
    if (sourceType === 'INPUT_EVENT' && targetType === 'FILTER_PROPERTY') {
      return false
    }
    // INPUT_ONLINE (设备上下线) → 不能连接任何 FILTER 节点
    if (sourceType === 'INPUT_ONLINE' && targetCategory === 'FILTER') {
      return false
    }
  }

  // FILTER 节点之后只能连接 OUTPUT 或 ALARM
  if (sourceCategory === 'FILTER') {
    if (targetCategory !== 'OUTPUT' && targetCategory !== 'ALARM') {
      return false
    }
  }

  // ALARM 节点之后只能连接 OUTPUT（自定义推送）
  if (sourceCategory === 'ALARM') {
    if (targetCategory !== 'OUTPUT') {
      return false
    }
  }

  return true
}

// 获取连接错误消息
function getConnectionErrorMessage(sourceType, targetType) {
  const sourceCategory = getNodeCategory(sourceType)
  const targetCategory = getNodeCategory(targetType)

  if (targetCategory === 'INPUT') {
    return '输入节点不能作为连接目标'
  }

  // OUTPUT 节点是终端
  if (sourceCategory === 'OUTPUT') {
    return '输出节点是终端节点，不能继续连接'
  }

  // 输入节点连接限制的错误提示
  if (sourceCategory === 'INPUT') {
    if (sourceType === 'INPUT_PROPERTY' && targetType === 'FILTER_EVENT_TYPE') {
      return '属性上报不能连接到事件类型过滤，请使用属性条件过滤'
    }
    if (sourceType === 'INPUT_EVENT' && targetType === 'FILTER_PROPERTY') {
      return '事件上报不能连接到属性条件过滤，请使用事件类型过滤'
    }
    if (sourceType === 'INPUT_ONLINE' && targetCategory === 'FILTER') {
      return '设备上下线事件不能使用过滤节点，可直接连接告警或输出节点'
    }
  }

  // FILTER 节点连接限制
  if (sourceCategory === 'FILTER') {
    if (targetCategory !== 'OUTPUT' && targetCategory !== 'ALARM') {
      return '过滤节点只能连接到输出或告警节点'
    }
  }

  // ALARM 节点连接限制
  if (sourceCategory === 'ALARM') {
    if (targetCategory !== 'OUTPUT') {
      return '告警节点只能连接到输出节点进行自定义推送'
    }
  }

  return '此连接不被允许'
}

// 节点分类
const nodeCategories = computed(() => {
  const categories = {}
  nodeTypes.value.forEach((type) => {
    if (!categories[type.category]) {
      categories[type.category] = []
    }
    categories[type.category].push(type)
  })
  return categories
})

// 获取连接类型（兼容 type 和 connectionType 字段名）
function getConnectionType(conn) {
  return conn.type || conn.connectionType || 'Success'
}

// 计算连接线
const connectionLines = computed(() => {
  return ruleChain.value.configuration.connections.map((conn) => {
    const sourceNode = ruleChain.value.configuration.nodes.find(n => n.id === conn.source)
    const targetNode = ruleChain.value.configuration.nodes.find(n => n.id === conn.target)

    if (!sourceNode || !targetNode)
      return null

    // 获取连接类型（兼容后端的 type 字段）
    const connType = getConnectionType(conn)

    // 计算源端口位置（根据连接类型和节点类型）
    const sourceX = sourceNode.x + 180 // 节点宽度
    let sourceY = sourceNode.y + 40 // 默认中心高度

    // 过滤节点根据 type 调整 Y 位置（与CSS中的端口位置一致）
    if (isFilterNode(sourceNode.type)) {
      if (connType === 'True') {
        sourceY = sourceNode.y + 24 // True 端口位置 (CSS: top: 24px)
      }
      else if (connType === 'False') {
        sourceY = sourceNode.y + 52 // False 端口位置 (CSS: top: 52px)
      }
    }

    const targetX = targetNode.x
    const targetY = targetNode.y + 40

    return {
      id: `${conn.source}-${conn.target}-${connType}`,
      source: { x: sourceX, y: sourceY },
      target: { x: targetX, y: targetY },
      sourceNode: conn.source,
      targetNode: conn.target,
      connectionType: connType,
    }
  }).filter(Boolean)
})

// 加载节点类型
async function loadNodeTypes() {
  try {
    const { data } = await ruleChainNodeTypesApi()
    const entries = Object.entries(data || {})
    const result = []
    for (const [category, types] of entries) {
      result.push(...types)
    }
    nodeTypes.value = result
  }
  catch (e) {
    console.error('加载节点类型失败', e)
  }
}

// 加载产品类型列表
async function loadProductTypes() {
  try {
    const { data } = await productTypeListApi()
    productTypeOptions.value = data || []
  }
  catch (e) {
    console.error('加载产品类型失败', e)
  }
}

// 加载消息接收配置
async function loadMessageReceiveOptions() {
  try {
    const { data } = await messageReceiveListApi()
    messageReceiveOptions.value = data || []
  }
  catch (e) {
    console.error('加载消息配置失败', e)
  }
}

// 加载推送配置
async function loadPushConfigOptions() {
  try {
    const { data } = await pushConfigListApi()
    pushConfigOptions.value = data || []
  }
  catch (e) {
    console.error('加载推送配置失败', e)
  }
}

// 加载产品列表(根据产品类型)
async function loadProducts() {
  if (!ruleChain.value.productTypeId) {
    productOptions.value = []
    return
  }
  try {
    const { data } = await productListApi({ productTypeId: ruleChain.value.productTypeId })
    productOptions.value = data || []
  }
  catch (e) {
    console.error('加载产品失败', e)
  }
}

// 加载设备列表(根据产品)
async function loadDevices() {
  if (!ruleChain.value.productId) {
    deviceOptions.value = []
    return
  }
  try {
    const { data } = await deviceListApi({ productId: ruleChain.value.productId })
    deviceOptions.value = data || []
  }
  catch (e) {
    console.error('加载设备失败', e)
  }
}

// 产品类型变更
function onProductTypeChange() {
  ruleChain.value.productId = null
  ruleChain.value.sourceId = null
  productOptions.value = []
  deviceOptions.value = []
  loadProducts()
}

// 产品变更
async function onProductChange() {
  ruleChain.value.sourceId = null
  deviceOptions.value = []
  // 如果数据源类型是产品,则sourceId就是productId
  if (ruleChain.value.sourceType === 'PRODUCT') {
    ruleChain.value.sourceId = ruleChain.value.productId
  }
  loadDevices()
  // 加载物模型属性
  await loadTslProperties()
}

// 加载物模型属性和事件
async function loadTslProperties() {
  if (!ruleChain.value.productId) {
    tslProperties.value = []
    tslEvents.value = []
    return
  }
  try {
    const { data } = await loadTslData(ruleChain.value.productId)
    const tslModel = data.data || data
    tslProperties.value = tslModel.properties || []
    tslEvents.value = tslModel.events || []
  }
  catch (e) {
    console.error('加载物模型失败', e)
    tslProperties.value = []
    tslEvents.value = []
  }
}

// 数据源类型变更
function onSourceTypeChange() {
  ruleChain.value.sourceId = null
  if (ruleChain.value.sourceType === 'PRODUCT') {
    ruleChain.value.sourceId = ruleChain.value.productId
  }
}

// 加载规则链详情
async function loadDetail() {
  if (!route.params.id || route.params.id === 'new')
    return
  try {
    const { data } = await ruleChainDetailApi(route.params.id)
    // 处理响应数据格式
    const detail = data.data || data
    Object.assign(ruleChain.value, detail)

    // 根据sourceType和sourceId反推级联选择字段(编辑模式回显)
    if (detail.sourceType === 'PRODUCT' && detail.sourceId) {
      // sourceId 就是 productId，需要找到对应的 productTypeId
      ruleChain.value.productId = detail.sourceId
      // 遍历所有产品类型，找到包含该产品的类型
      for (const pt of productTypeOptions.value) {
        const { data: products } = await productListApi({ productTypeId: pt.id })
        const product = (products || []).find(p => p.id === detail.sourceId)
        if (product) {
          ruleChain.value.productTypeId = pt.id
          productOptions.value = products
          break
        }
      }
    }
    else if (detail.sourceType === 'DEVICE' && detail.sourceId) {
      // sourceId 是设备ID，需要找到对应的 productId 和 productTypeId
      // 遍历所有产品类型和产品，找到包含该设备的产品
      for (const pt of productTypeOptions.value) {
        const { data: products } = await productListApi({ productTypeId: pt.id })
        for (const p of products || []) {
          const { data: devices } = await deviceListApi({ productId: p.id })
          const device = (devices || []).find(d => d.id === detail.sourceId)
          if (device) {
            ruleChain.value.productTypeId = pt.id
            ruleChain.value.productId = p.id
            productOptions.value = products
            deviceOptions.value = devices
            break
          }
        }
        if (ruleChain.value.productId)
          break
      }
    }
    // 加载物模型属性
    if (ruleChain.value.productId) {
      await loadTslProperties()
    }
  }
  catch (e) {
    console.error('加载规则链失败', e)
  }
}

// 生成唯一ID
function generateId() {
  return `node_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
}

// 开始从节点库拖拽
function startDragFromPanel(type, event) {
  event.dataTransfer.setData('nodeType', JSON.stringify(type))
  event.dataTransfer.effectAllowed = 'copy'
}

// 画布拖拽进入
function onCanvasDragOver(event) {
  event.preventDefault()
  event.dataTransfer.dropEffect = 'copy'
}

// 画布放置
function onCanvasDrop(event) {
  event.preventDefault()
  const nodeTypeData = event.dataTransfer.getData('nodeType')
  if (!nodeTypeData)
    return
  const type = JSON.parse(nodeTypeData)
  const rect = canvasRef.value.getBoundingClientRect()
  const x = event.clientX - rect.left - 90 // 居中
  const y = event.clientY - rect.top - 30
  addNode(type, x, y)
}

// 点击添加节点
function onNodeClick(type, _event) {
  const x = 100 + Math.random() * 200
  const y = 100 + Math.random() * 200
  addNode(type, x, y)
}

// 添加节点
function addNode(type, x, y) {
  const node = {
    id: generateId(),
    type: type.type,
    name: type.name,
    x: Math.max(0, x),
    y: Math.max(0, y),
    config: {},
    color: type.color || '#6366f1',
  }
  ruleChain.value.configuration.nodes.push(node)
  selectedNode.value = node
}

// 开始拖拽节点
function startDragNode(node, event) {
  event.stopPropagation()
  dragState.value = {
    isDragging: true,
    dragNode: node,
    dragType: 'move',
    startX: event.clientX,
    startY: event.clientY,
    offsetX: node.x,
    offsetY: node.y,
  }
  document.addEventListener('mousemove', onDragNode)
  document.addEventListener('mouseup', stopDragNode)
}
// 拖拽节点中
function onDragNode(event) {
  if (!dragState.value.isDragging)
    return
  const dx = event.clientX - dragState.value.startX
  const dy = event.clientY - dragState.value.startY
  dragState.value.dragNode.x = Math.max(0, dragState.value.offsetX + dx)
  dragState.value.dragNode.y = Math.max(0, dragState.value.offsetY + dy)
}
// 停止拖拽节点
function stopDragNode() {
  dragState.value.isDragging = false
  document.removeEventListener('mousemove', onDragNode)
  document.removeEventListener('mouseup', stopDragNode)
}
// 开始连接线拖拽
function startConnection(node, portType, event) {
  event.stopPropagation()
  event.preventDefault()
  // portType: 'input', 'True', 'False', 'Success'
  connectionDrag.value = {
    isDragging: true,
    sourceNode: node,
    sourcePort: portType,
    connectionType: portType === 'input' ? 'input' : portType, // True/False/Success
  }
  document.addEventListener('mousemove', onConnectionDrag)
  document.addEventListener('mouseup', stopConnection)
}
// 连接线拖拽中
function onConnectionDrag(event) {
  if (!connectionDrag.value.isDragging)
    return
  const rect = canvasRef.value.getBoundingClientRect()
  const node = connectionDrag.value.sourceNode

  // 计算源端口位置（与CSS中的端口位置一致）
  let sourceY = node.y + 40 // 默认中心
  if (connectionDrag.value.connectionType === 'True') {
    sourceY = node.y + 24 // True 端口位置 (CSS: top: 24px)
  }
  else if (connectionDrag.value.connectionType === 'False') {
    sourceY = node.y + 52 // False 端口位置 (CSS: top: 52px)
  }

  tempConnection.value = {
    sourceX: node.x + 180,
    sourceY,
    targetX: event.clientX - rect.left,
    targetY: event.clientY - rect.top,
  }
}
// 停止连接线拖拽
function stopConnection(event) {
  if (connectionDrag.value.isDragging) {
    // 检查是否放在某个节点的输入端口上
    const rect = canvasRef.value.getBoundingClientRect()
    const dropX = event.clientX - rect.left
    const dropY = event.clientY - rect.top
    // 查找目标节点
    const targetNode = ruleChain.value.configuration.nodes.find((node) => {
      return (
        dropX >= node.x
        && dropX <= node.x + 180
        && dropY >= node.y
        && dropY <= node.y + 80
        && node.id !== connectionDrag.value.sourceNode.id
        && !isInputNode(node.type) // 输入节点不能作为目标
      )
    })

    // 确定连接类型
    let connectionType = connectionDrag.value.connectionType
    if (connectionType === 'input') {
      connectionType = 'Success' // 从输入端口拖出的默认是 Success
    }

    if (targetNode) {
      // 验证连接是否有效
      const sourceType = connectionDrag.value.sourceNode.type
      const targetType = targetNode.type
      if (!isValidConnection(sourceType, targetType, connectionType)) {
        ElMessage.warning(getConnectionErrorMessage(sourceType, targetType))
        connectionDrag.value.isDragging = false
        tempConnection.value = null
        document.removeEventListener('mousemove', onConnectionDrag)
        document.removeEventListener('mouseup', stopConnection)
        return
      }

      // 检查是否已存在相同连接（使用 type 字段）
      const exists = ruleChain.value.configuration.connections.some(
        c =>
          c.source === connectionDrag.value.sourceNode.id
          && c.target === targetNode.id
          && getConnectionType(c) === connectionType,
      )
      if (!exists) {
        ruleChain.value.configuration.connections.push({
          source: connectionDrag.value.sourceNode.id,
          target: targetNode.id,
          type: connectionType, // 使用 type 字段与后端保持一致
        })
      }
    } else {
      // 没有连接到节点，显示节点创建菜单
      showNodeCreateMenu(event.clientX, event.clientY, dropX, dropY, connectionType)
    }
  }
  connectionDrag.value.isDragging = false
  tempConnection.value = null
  document.removeEventListener('mousemove', onConnectionDrag)
  document.removeEventListener('mouseup', stopConnection)
}

// 显示节点创建菜单
function showNodeCreateMenu(clientX, clientY, canvasX, canvasY, connectionType) {
  const sourceNode = connectionDrag.value.sourceNode
  const sourceCategory = getNodeCategory(sourceNode.type)

  // 根据源节点类型过滤可连接的节点类型
  // INPUT 节点只能连到 FILTER/ALARM/OUTPUT
  // FILTER/ALARM/OUTPUT 可以互相连接
  const availableTypes = nodeTypes.value.filter((type) => {
    const targetCategory = getNodeCategory(type.type)
    // 排除 INPUT 类型（不能作为目标）
    if (targetCategory === 'INPUT') return false
    // 验证连接有效性
    return isValidConnection(sourceNode.type, type.type, connectionType)
  })

  if (availableTypes.length === 0) {
    ElMessage.warning('没有可连接的节点类型')
    return
  }

  nodeCreateMenu.value = {
    visible: true,
    x: clientX,
    y: clientY,
    canvasX,
    canvasY,
    sourceNode,
    connectionType,
    availableTypes,
  }
}

// 从菜单创建节点并连接
function createNodeAndConnect(type) {
  // 在菜单位置创建节点
  const node = {
    id: generateId(),
    type: type.type,
    name: type.name,
    x: Math.max(0, nodeCreateMenu.value.canvasX - 90), // 居中
    y: Math.max(0, nodeCreateMenu.value.canvasY - 40),
    config: {},
    color: type.color || '#6366f1',
  }
  ruleChain.value.configuration.nodes.push(node)

  // 创建连接
  ruleChain.value.configuration.connections.push({
    source: nodeCreateMenu.value.sourceNode.id,
    target: node.id,
    type: nodeCreateMenu.value.connectionType,
  })

  // 选中新节点
  selectedNode.value = node

  // 关闭菜单
  closeNodeCreateMenu()
}

// 关闭节点创建菜单
function closeNodeCreateMenu() {
  nodeCreateMenu.value.visible = false
}
// 删除节点
function deleteNode(nodeId) {
  ruleChain.value.configuration.nodes = ruleChain.value.configuration.nodes.filter(
    n => n.id !== nodeId,
  )
  ruleChain.value.configuration.connections = ruleChain.value.configuration.connections.filter(
    c => c.source !== nodeId && c.target !== nodeId,
  )
  if (selectedNode.value?.id === nodeId) {
    selectedNode.value = null
  }
}

// 更新节点配置（同步更新 selectedNode 和 nodes 数组）
function updateNode(updatedNode) {
  selectedNode.value = updatedNode
  const index = ruleChain.value.configuration.nodes.findIndex(n => n.id === updatedNode.id)
  if (index !== -1) {
    ruleChain.value.configuration.nodes[index] = updatedNode
  }
}

// 删除连接
function deleteConnection(sourceId, targetId, connectionType) {
  // 标准化 connectionType，将 undefined/null 视为 'Success'
  const normalizedType = connectionType || 'Success'
  ruleChain.value.configuration.connections = ruleChain.value.configuration.connections.filter(
    (c) => {
      const cType = getConnectionType(c)
      return !(c.source === sourceId && c.target === targetId && cType === normalizedType)
    },
  )
}
// 生成贝塞尔曲线路径
function generateBezierPath(x1, y1, x2, y2) {
  const cpOffset = Math.min(Math.abs(x2 - x1) * 0.5, 100)
  return `M ${x1} ${y1} C ${x1 + cpOffset} ${y1}, ${x2 - cpOffset} ${y2}, ${x2} ${y2}`
}
// 保存
async function handleSave() {
  saving.value = true
  try {
    // 构建提交数据 - 为每个节点的 config 添加 nodeType 以支持后端多态反序列化
    const configuration = {
      nodes: ruleChain.value.configuration.nodes.map(node => ({
        ...node,
        config: node.config ? { ...node.config, nodeType: node.type } : null,
      })),
      connections: ruleChain.value.configuration.connections,
    }

    const submitData = {
      ...ruleChain.value,
      configuration,
      // 如果sourceType是PRODUCT,sourceId就是productId
      sourceId: ruleChain.value.sourceType === 'PRODUCT'
        ? ruleChain.value.productId
        : ruleChain.value.sourceId,
    }
    if (isEdit.value) {
      await ruleChainUpdateApi(submitData)
    }
    else {
      await ruleChainAddApi(submitData)
    }
    router.push('/rule-chain')
  }
  catch (e) {
    console.error('保存失败', e)
  }
  finally {
    saving.value = false
  }
}
// 返回
function handleBack() {
  router.push('/rule-chain')
}
onMounted(async () => {
  await loadNodeTypes()
  await loadProductTypes()
  await loadMessageReceiveOptions()
  await loadPushConfigOptions()
  if (route.params.id && route.params.id !== 'new') {
    await loadDetail()
  }
})
</script>

<template>
  <div class="rule-chain-editor">
    <!-- 顶部工具栏 -->
    <div class="editor-header glass-card">
      <div class="header-left">
        <el-button @click="handleBack">
          <span>← 返回</span>
        </el-button>
        <span class="title">{{ isEdit ? '编辑规则引擎' : '新建规则引擎' }}</span>
      </div>
      <div class="header-right">
        <el-button type="primary" :loading="saving" @click="handleSave">
          保存
        </el-button>
      </div>
    </div>

    <div class="editor-main">
      <!-- 左侧节点面板 -->
      <div class="node-panel glass-card">
        <div class="panel-title">
          节点库
        </div>

        <!-- 巻加产品类型和产品的辅助字段 -->
        <el-form label-width="70px" size="small">
          <el-form-item label="名称">
            <el-input v-model="ruleChain.name" placeholder="规则名称" />
          </el-form-item>
          <el-form-item label="数据源">
            <el-select v-model="ruleChain.sourceType" @change="onSourceTypeChange">
              <el-option label="产品" value="PRODUCT" />
              <el-option label="特定设备" value="DEVICE" />
            </el-select>
          </el-form-item>
          <el-form-item label="产品类型">
            <el-select
              v-model="ruleChain.productTypeId"
              placeholder="请选择产品类型"
              filterable
              clearable
              @change="onProductTypeChange"
            >
              <el-option
                v-for="item in productTypeOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="产品">
            <el-select
              v-model="ruleChain.productId"
              placeholder="请先选择产品类型"
              filterable
              clearable
              :disabled="!ruleChain.productTypeId"
              @change="onProductChange"
            >
              <el-option
                v-for="item in productOptions"
                :key="item.id"
                :label="`${item.model} (${item.manufacturer})`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="ruleChain.sourceType === 'DEVICE'" label="设备">
            <el-select
              v-model="ruleChain.sourceId"
              placeholder="请先选择产品"
              filterable
              clearable
              :disabled="!ruleChain.productId"
            >
              <el-option
                v-for="item in deviceOptions"
                :key="item.id"
                :label="`${item.deviceName} (${item.deviceKey})`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-form>

        <!-- 节点分类 -->
        <div v-for="(types, category) in nodeCategories" :key="category" class="node-category">
          <div class="category-title">
            {{ category }}
          </div>
          <div class="node-list">
            <div
              v-for="type in types"
              :key="type.type"
              class="node-item"
              :style="{ borderLeftColor: type.color || '#6366f1' }"
              draggable="true"
              @dragstart="startDragFromPanel(type, $event)"
              @click="onNodeClick(type, $event)"
            >
              <span class="node-name">{{ type.name }}</span>
              <span class="node-desc">{{ type.description }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 中间画布 -->
      <div
        ref="canvasRef"
        class="canvas-panel glass-card"
        @dragover="onCanvasDragOver"
        @drop="onCanvasDrop"
      >
        <!-- SVG连接线层 -->
        <svg ref="svgRef" class="connections-svg">
          <!-- 已有连接线 -->
          <g v-for="line in connectionLines" :key="line.id" class="connection-group">
            <!-- 透明宽点击区域 -->
            <path
              :d="generateBezierPath(line.source.x, line.source.y, line.target.x, line.target.y)"
              class="connection-hitbox"
              @click.stop="deleteConnection(line.sourceNode, line.targetNode, line.connectionType)"
            />
            <!-- 可见连接线 -->
            <path
              :d="generateBezierPath(line.source.x, line.source.y, line.target.x, line.target.y)"
              class="connection-line"
              :class="`connection-${(line.connectionType || 'Success').toLowerCase()}`"
            />
            <!-- 箭头 -->
            <polygon
              :points="`${line.target.x - 8},${line.target.y - 5} ${line.target.x},${line.target.y} ${line.target.x - 8},${line.target.y + 5}`"
              class="connection-arrow"
              :class="`connection-${(line.connectionType || 'Success').toLowerCase()}`"
            />
            <!-- 删除提示（hover时显示） -->
            <circle
              :cx="(line.source.x + line.target.x) / 2"
              :cy="(line.source.y + line.target.y) / 2"
              r="14"
              class="delete-hint"
              @click.stop="deleteConnection(line.sourceNode, line.targetNode, line.connectionType)"
            />
            <text
              :x="(line.source.x + line.target.x) / 2"
              :y="(line.source.y + line.target.y) / 2"
              class="delete-text"
              @click.stop="deleteConnection(line.sourceNode, line.targetNode, line.connectionType)"
            >×</text>
          </g>
          <!-- 临时连接线(拖拽中) -->
          <path
            v-if="tempConnection"
            :d="generateBezierPath(
              tempConnection.sourceX,
              tempConnection.sourceY,
              tempConnection.targetX,
              tempConnection.targetY,
            )"
            class="connection-line temp"
            :class="`connection-${(connectionDrag.connectionType || 'Success').toLowerCase()}`"
          />
        </svg>

        <div v-if="ruleChain.configuration.nodes.length === 0" class="empty-tip">
          从左侧拖拽节点到画布,或点击节点添加
        </div>

        <!-- 节点 -->
        <div
          v-for="node in ruleChain.configuration.nodes"
          :key="node.id"
          class="canvas-node"
          :class="{
            'selected': selectedNode?.id === node.id,
            'is-input': isInputNode(node.type),
            'is-filter': isFilterNode(node.type),
          }"
          :style="{ 'left': `${node.x}px`, 'top': `${node.y}px`, '--node-color': node.color }"
          @click="selectedNode = selectedNode?.id === node.id ? null : node"
          @mousedown="startDragNode(node, $event)"
        >
          <!-- 输入端口 (INPUT类型节点不显示) -->
          <div v-if="!isInputNode(node.type)" class="node-port input" @mousedown="startConnection(node, 'input', $event)">
            <span class="port-dot" />
          </div>

          <div class="node-header">
            <el-button link type="danger" size="small" class="delete-btn" @click.stop="deleteNode(node.id)">
              <el-icon :size="14">
                <Close />
              </el-icon>
            </el-button>
            <span class="node-name-text">{{ node.name }}</span>
          </div>
          <div class="node-body">
            <div class="node-type">
              {{ node.type }}
            </div>
          </div>

          <!-- 输出端口 - 过滤节点有两个：True/False -->
          <template v-if="isFilterNode(node.type)">
            <div class="node-port output true" @mousedown="startConnection(node, 'True', $event)">
              <span class="port-label">T</span>
              <span class="port-dot" />
            </div>
            <div class="node-port output false" @mousedown="startConnection(node, 'False', $event)">
              <span class="port-label">F</span>
              <span class="port-dot" />
            </div>
          </template>
          <!-- 普通输出端口（OUTPUT节点是终端，不显示输出端口） -->
          <div v-else-if="getNodeCategory(node.type) !== 'OUTPUT'" class="node-port output" @mousedown="startConnection(node, 'Success', $event)">
            <span class="port-dot" />
          </div>
        </div>
      </div>

      <!-- 右侧配置面板 -->
      <div v-if="selectedNode" class="config-panel glass-card">
        <div class="panel-title">
          节点配置
        </div>
        <div class="config-content">
          <NodeConfigPanel
            :selected-node="selectedNode"
            :rule-chain="ruleChain"
            :tsl-properties="tslProperties"
            :tsl-events="tslEvents"
            :upstream-node="upstreamInputNode"
            :product-options="productOptions"
            :product-type-options="productTypeOptions"
            :message-receive-options="messageReceiveOptions"
            :push-config-options="pushConfigOptions"
            @update:node="updateNode"
          />
        </div>
      </div>
    </div>

    <!-- 节点创建菜单 -->
    <Teleport to="body">
      <div
        v-if="nodeCreateMenu.visible"
        class="node-create-menu glass-card"
        :style="{ left: `${nodeCreateMenu.x}px`, top: `${nodeCreateMenu.y}px` }"
      >
        <div class="menu-header">
          <span>选择要创建的节点</span>
          <el-button link size="small" @click="closeNodeCreateMenu">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div class="menu-content">
          <div
            v-for="type in nodeCreateMenu.availableTypes"
            :key="type.type"
            class="menu-item"
            :style="{ borderLeftColor: type.color || '#6366f1' }"
            @click="createNodeAndConnect(type)"
          >
            <span class="item-name">{{ type.name }}</span>
            <span class="item-desc">{{ type.description }}</span>
          </div>
        </div>
      </div>
      <!-- 点击遮罩关闭菜单 -->
      <div
        v-if="nodeCreateMenu.visible"
        class="menu-overlay"
        @click="closeNodeCreateMenu"
      />
    </Teleport>
  </div>
</template>

<style scoped lang="scss">
.rule-chain-editor {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: var(--space-md);
  gap: var(--space-md);
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-md) var(--space-lg);

  .header-left {
    display: flex;
    align-items: center;
    gap: var(--space-md);

    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }
}

.editor-main {
  flex: 1;
  display: flex;
  gap: var(--space-md);
  min-height: 0;
}
.node-panel {
  width: 280px;
  padding: var(--space-md);
  overflow-y: auto;

  .panel-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: var(--space-md);
    padding-bottom: var(--space-sm);
    border-bottom: 1px solid var(--iot-color-border);
  }
  .config-section {
    margin-bottom: var(--space-lg);
  }
  .section-title,
  .category-title {
    font-size: 14px;
    font-weight: 500;
    color: var(--iot-color-text-secondary);
    margin-bottom: var(--space-sm);
  }
  .node-category {
    margin-bottom: var(--space-md);
  }
  .node-list {
    display: flex;
    flex-direction: column;
    gap: var(--space-xs);
  }
  .node-item {
    padding: var(--space-sm);
    background: rgba(255, 255, 255, 0.5);
    border-radius: var(--radius-sm);
    border-left: 3px solid var(--iot-color-primary);
    cursor: grab;
    transition: all 0.2s ease-out;

    &:hover {
      background: rgba(255, 255, 255, 0.8);
      transform: translateX(2px);
      box-shadow: 0 2px 8px rgba(99, 102, 241, 0.15);
    }
    &:active {
      cursor: grabbing;
    }
    .node-name {
      display: block;
      font-size: 13px;
      font-weight: 500;
    }
    .node-desc {
      display: block;
      font-size: 11px;
      color: var(--iot-color-text-muted);
      margin-top: 2px;
    }
  }
}
.canvas-panel {
  flex: 1;
  position: relative;
  overflow: hidden;
  background-image: linear-gradient(rgba(0, 0, 0, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.03) 1px, transparent 1px);
  background-size: 20px 20px;
  .empty-tip {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: var(--iot-color-text-muted);
    font-size: 14px;
    pointer-events: none;
  }
  .connections-svg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;

    // 连接线组 - hover 效果
    .connection-group {
      pointer-events: auto;
      cursor: pointer;

      &:hover .connection-line {
        stroke-width: 3;
      }
      &:hover .delete-hint,
      &:hover .delete-text {
        opacity: 1;
      }
    }

    // 透明点击区域（更宽）
    .connection-hitbox {
      fill: transparent;
      stroke: transparent;
      stroke-width: 24px;
      pointer-events: all;
      cursor: pointer;
    }

    .connection-line {
      fill: none;
      stroke: var(--iot-color-primary);
      stroke-width: 2;
      pointer-events: none;
      transition: stroke-width 0.15s ease-out;

      // 不同连接类型的颜色
      &.connection-true {
        stroke: #10b981; // 绿色 - True
      }
      &.connection-false {
        stroke: #ef4444; // 红色 - False
      }
      &.connection-success {
        stroke: var(--iot-color-primary); // 主色 - Success
      }

      &.temp {
        stroke-dasharray: 5, 5;
        opacity: 0.7;
        pointer-events: none;
      }
    }

    // hover 时连接线变粗
    .connection-group:hover .connection-line {
      stroke-width: 3;
      filter: drop-shadow(0 0 3px currentColor);
    }

    .connection-arrow {
      fill: var(--iot-color-primary);
      pointer-events: none;

      &.connection-true {
        fill: #10b981;
      }
      &.connection-false {
        fill: #ef4444;
      }
      &.connection-success {
        fill: var(--iot-color-primary);
      }
    }
    // 删除提示（hover 时显示）
    .delete-hint {
      fill: rgba(239, 68, 68, 0.95);
      stroke: white;
      stroke-width: 2;
      opacity: 0;
      transition: opacity 0.2s;
      pointer-events: none; // 默认不拦截事件，让 hitbox 接收点击
      cursor: pointer;
    }
    .delete-text {
      fill: white;
      font-size: 16px;
      font-weight: bold;
      text-anchor: middle;
      dominant-baseline: central;
      opacity: 0;
      transition: opacity 0.2s;
      pointer-events: none; // 默认不拦截事件，让 hitbox 接收点击
      cursor: pointer;
      user-select: none;
    }
    // hover 整个组时显示删除提示并启用点击
    .connection-group:hover .delete-hint,
    .connection-group:hover .delete-text {
      opacity: 1;
      pointer-events: auto; // hover 时才启用点击
    }
  }
  .canvas-node {
    position: absolute;
    width: 180px;
    background: white;
    border-radius: var(--radius-md);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    border: 2px solid var(--iot-color-border);
    cursor: move;
    transition:
      box-shadow 0.2s ease-out,
      border-color 0.2s ease-out;
    &:hover {
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
    }
    &.selected {
      border-color: var(--iot-color-primary);
      box-shadow:
        0 6px 20px rgba(0, 0, 0, 0.15),
        0 0 0 3px rgba(99, 102, 241, 0.2);
    }
    // 输入节点样式（绿色边框）
    &.is-input {
      border-color: #10b981;
    }
    // 过滤节点样式
    &.is-filter {
      // 过滤节点有额外的输出端口
    }
    .node-port {
      position: absolute;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: crosshair;
      z-index: 10;
      &.input {
        left: -8px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
      }
      &.output {
        right: -8px;
        top: 50%;
        transform: translateY(-50%);
        width: 16px;
        height: 16px;
      }
      .port-label {
        position: absolute;
        right: 18px;
        font-size: 11px;
        font-weight: 700;
        color: var(--iot-color-text-muted);
      }
      .port-dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background: white;
        border: 2px solid var(--iot-color-primary);
        transition: all 0.15s ease-out;
      }
      &:hover .port-dot {
        transform: scale(1.3);
        background: var(--iot-color-primary);
      }
      // 过滤节点的双输出端口 - 放在后面以覆盖默认样式
      &.output.true {
        top: 24px; // 靠近顶部
        .port-dot {
          border-color: #10b981;
          background: #10b981;
        }
        .port-label {
          color: #10b981 !important;
          font-weight: 700;
        }
        &:hover .port-dot {
          transform: scale(1.3);
          background: #10b981;
          box-shadow: 0 0 6px rgba(16, 185, 129, 0.5);
        }
      }
      &.output.false {
        top: 52px; // 靠近底部
        .port-dot {
          border-color: #ef4444;
          background: #ef4444;
        }
        .port-label {
          color: #ef4444 !important;
          font-weight: 700;
        }
        &:hover .port-dot {
          transform: scale(1.3);
          background: #ef4444;
          box-shadow: 0 0 6px rgba(239, 68, 68, 0.5);
        }
      }
    }
    .node-header {
      display: flex;
      align-items: center;
      padding: var(--space-sm) var(--space-md);
      background: linear-gradient(135deg, var(--node-color, var(--iot-color-primary)), var(--iot-color-accent));
      color: white;
      border-radius: var(--radius-md) var(--radius-md) 0 0;
      font-size: 13px;
      font-weight: 500;
      gap: var(--space-sm);

      .delete-btn {
        opacity: 0.7;
        color: white;
        padding: 2px;
        &:hover {
          opacity: 1;
          color: #fecaca;
        }
      }
      .node-name-text {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
    .node-body {
      padding: var(--space-sm) var(--space-md);
      .node-type {
        font-size: 11px;
        color: var(--iot-color-text-muted);
      }
    }
  }
}
.config-panel {
  width: 300px;
  padding: var(--space-md);
  overflow-y: auto;
  .panel-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: var(--space-md);
    padding-bottom: var(--space-sm);
    border-bottom: 1px solid var(--iot-color-border);
  }
  .config-content {
    :deep(.el-form-item) {
      margin-bottom: var(--space-md);
    }
  }
  // 表单提示样式
  .form-tip {
    font-size: 12px;
    color: var(--iot-color-text-muted);
    margin-top: 4px;
  }
  // 属性信息样式
  .property-info {
    display: flex;
    flex-wrap: wrap;
    gap: var(--space-sm);
    font-size: 12px;
    color: var(--iot-color-text-secondary);
    span {
      padding: 2px 8px;
      background: rgba(99, 102, 241, 0.1);
      border-radius: var(--radius-sm);
    }
  }
}
// 暗色模式适配
:global(.dark) {
  .canvas-panel {
    background-image: linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  }
  .canvas-node {
    background: rgba(30, 41, 59, 0.9);
    border-color: rgba(255, 255, 255, 0.1);
  }
  .node-item {
    background: rgba(30, 41, 59, 0.5);
    &:hover {
      background: rgba(30, 41, 59, 0.8);
    }
  }
}

// 节点创建菜单（全局样式，因为使用 Teleport）
.node-create-menu {
  position: fixed;
  z-index: 9999;
  min-width: 200px;
  max-width: 280px;
  max-height: 400px;
  overflow-y: auto;
  padding: 0;
  border-radius: var(--radius-lg);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  animation: menuFadeIn 0.15s ease-out;

  .menu-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: var(--space-sm) var(--space-md);
    border-bottom: 1px solid var(--iot-color-border);
    font-size: 13px;
    font-weight: 500;
    color: var(--iot-color-text-secondary);
  }

  .menu-content {
    padding: var(--space-sm);
  }

  .menu-item {
    padding: var(--space-sm) var(--space-md);
    border-radius: var(--radius-sm);
    border-left: 3px solid var(--iot-color-primary);
    cursor: pointer;
    transition: all 0.15s ease-out;
    margin-bottom: var(--space-xs);

    &:hover {
      background: var(--iot-glass-bg-hover);
      transform: translateX(2px);
    }

    &:last-child {
      margin-bottom: 0;
    }

    .item-name {
      display: block;
      font-size: 13px;
      font-weight: 500;
      color: var(--iot-color-text-primary);
    }

    .item-desc {
      display: block;
      font-size: 11px;
      color: var(--iot-color-text-muted);
      margin-top: 2px;
    }
  }
}

.menu-overlay {
  position: fixed;
  inset: 0;
  z-index: 9998;
  background: transparent;
}

@keyframes menuFadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
