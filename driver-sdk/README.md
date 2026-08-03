# Driver SDK

自定义驱动开发包，外部开发者无需依赖完整 iot-server 即可编写设备协议驱动。

## 快速开始

```bash
# 复制 driver-sdk 目录到你的项目
cp -r driver-sdk ~/my-driver

# 修改 pom.xml 的 groupId / artifactId
# 编写 ProtocolDecoder + DeviceKeyParser 实现
mvn clean package
```

## 核心接口

SDK 只依赖两个模块，包含全部开发所需的接口和模型：

| 模块 | 关键接口 | 用途 |
|------|---------|------|
| `iot-core` | `DeviceKeyParser` | 从上报数据解析设备唯一标识 |
| `iot-core` | `DeviceTransport` | 设备连接传输层抽象 |
| `iot-protocol-core` | `ProtocolDecoder` | 协议解码（上行）/ 编码（下行） |
| `iot-protocol-core` | `DeviceConnection` | 设备连接操作（发送/断开） |

## 数据模型

```
DeviceRequest  →  ProtocolDecoder.decode()  →  DecodeResult(List<DeviceData>)
EncoderMessage →  ProtocolDecoder.encode()  →  EncoderResult(byte[])
```

- `DeviceData(identifier, dataType, value)` — 单个属性值
- `DecodeResult.success(dataList)` — 解码成功
- `EncoderMessage` — 下行指令（服务调用/属性设置）

## 示例：JSON 协议

设备上报格式：
```json
{"deviceKey":"sensor-001","temperature":25.5,"humidity":60}
```

参见 `ExampleJsonProtocolDecoder` + `ExampleDeviceKeyParser`。

## 部署方式

### 方式一：Groovy 脚本（运行时热加载）

平台内置 Groovy 引擎，将解码器写成 `.groovy` 脚本，通过管理界面录入即可，无需编译部署：

1. 实现 `ProtocolDecoder` 接口的 Groovy 脚本
2. 平台「协议管理」页面录入脚本内容
3. 设备使用此 `protocolKey` 即自动走该解码器

### 方式二：Java JAR 插件

1. 用本 SDK 编写 `ProtocolDecoder` 实现
2. `mvn package` 打 JAR
3. JAR 放入 `iot-server/plugins/` 目录
4. 实现 `META-INF/services/com.github.dingdaoyi.proto.inter.ProtocolDecoder` SPI 注册

## 物模型映射

解码返回的 `DeviceData.identifier` 必须与物模型（TslModel）的属性标识一致，平台自动映射存储。
