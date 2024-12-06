<template>
  <div class="editorContainer">
    <div ref="editorContainer" class="wh-full"></div>
  </div>
</template>

<script setup>
import { ref, onBeforeUnmount } from 'vue'
import * as monaco from 'monaco-editor'
import { loadTslData } from '../../../api/index.js'

const props = defineProps(['productId'])
const jsonData = ref({
  id: 1,
  name: 'DeviceModel',
  properties: [
    { name: 'temperature', type: 'float', unit: '°C' },
    { name: 'status', type: 'string', enum: ['on', 'off'] }
  ]
})

const editorContainer = ref(null)
let editor = null

onBeforeUnmount(() => {
  if (editor) {
    editor.dispose()
  }
})

loadTslData(props.productId)
  .then(({ data }) => {
    jsonData.value = data
    editor = monaco.editor.create(editorContainer.value, {
      value: JSON.stringify(jsonData.value, null, 2),
      language: 'json',
      theme: 'vs-dark',
      readOnly: true,
      automaticLayout: true
    })
  })
  .catch((error) => {
    console.error('Failed to load TSL data:', error)
  })
</script>

<style>
.editorContainer {
  height: calc(80vh);
}
</style>
