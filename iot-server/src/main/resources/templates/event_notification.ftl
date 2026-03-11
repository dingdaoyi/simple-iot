<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!'设备事件通知'}</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            line-height: 1.6;
            color: #1f2937;
            background-color: #f3f4f6;
            padding: 20px;
        }
        .email-wrapper {
            max-width: 600px;
            margin: 0 auto;
            background: #ffffff;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
        }
        .email-header {
            background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
            padding: 24px 32px;
            color: #ffffff;
        }
        .email-header h1 {
            font-size: 20px;
            font-weight: 600;
            margin: 0;
        }
        .email-header p {
            font-size: 14px;
            opacity: 0.9;
            margin-top: 4px;
        }
        .email-body {
            padding: 32px;
        }
        .greeting {
            font-size: 16px;
            color: #374151;
            margin-bottom: 24px;
        }
        .content-box {
            background: #fef3c7;
            border-left: 4px solid #f59e0b;
            padding: 16px 20px;
            border-radius: 0 8px 8px 0;
            margin-bottom: 24px;
        }
        .content-box p {
            font-size: 15px;
            color: #92400e;
            margin: 0;
            white-space: pre-wrap;
        }
        .info-section {
            background: #f9fafb;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 24px;
        }
        .info-section h3 {
            font-size: 14px;
            font-weight: 600;
            color: #6b7280;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            margin-bottom: 12px;
        }
        .info-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 12px;
        }
        .info-item {
            display: flex;
            flex-direction: column;
        }
        .info-label {
            font-size: 12px;
            color: #9ca3af;
            margin-bottom: 2px;
        }
        .info-value {
            font-size: 14px;
            color: #1f2937;
            font-weight: 500;
        }
        .event-data {
            margin-top: 16px;
            padding-top: 16px;
            border-top: 1px solid #e5e7eb;
        }
        .event-data h4 {
            font-size: 14px;
            font-weight: 600;
            color: #6b7280;
            margin-bottom: 12px;
        }
        .event-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .event-list li {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 8px 0;
            border-bottom: 1px dashed #e5e7eb;
        }
        .event-list li:last-child {
            border-bottom: none;
        }
        .event-key {
            font-size: 14px;
            color: #6b7280;
        }
        .event-value {
            font-size: 14px;
            color: #1f2937;
            font-weight: 500;
            background: #e0e7ff;
            padding: 2px 8px;
            border-radius: 4px;
        }
        .email-footer {
            background: #f9fafb;
            padding: 20px 32px;
            border-top: 1px solid #e5e7eb;
        }
        .email-footer p {
            font-size: 13px;
            color: #6b7280;
            margin: 0;
        }
        .email-footer a {
            color: #6366f1;
            text-decoration: none;
        }
        @media only screen and (max-width: 480px) {
            body {
                padding: 10px;
            }
            .email-header, .email-body, .email-footer {
                padding-left: 20px;
                padding-right: 20px;
            }
            .info-grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<div class="email-wrapper">
    <div class="email-header">
        <h1>${title!'设备事件通知'}</h1>
        <p>IoT 物联网平台</p>
    </div>

    <div class="email-body">
        <p class="greeting">尊敬的用户，您好！</p>

        <div class="content-box">
            <p>${content!'您的设备触发了事件，请及时处理。'}</p>
        </div>

        <div class="info-section">
            <h3>设备信息</h3>
            <div class="info-grid">
                <div class="info-item">
                    <span class="info-label">设备名称</span>
                    <span class="info-value">${deviceName!'-'}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">设备标识</span>
                    <span class="info-value">${deviceKey!'-'}</span>
                </div>
                <div class="info-item">
                    <span class="info-label">事件时间</span>
                    <span class="info-value">${eventTime!'-'}</span>
                </div>
                <#if eventTypeName??>
                <div class="info-item">
                    <span class="info-label">事件类型</span>
                    <span class="info-value">${eventTypeName}</span>
                </div>
                </#if>
            </div>

            <#if eventContent?? && (eventContent?size > 0)>
            <div class="event-data">
                <h4>事件数据</h4>
                <ul class="event-list">
                    <#list eventContent?keys as key>
                    <li>
                        <span class="event-key">${key}</span>
                        <span class="event-value">${eventContent[key]!'-'}</span>
                    </li>
                    </#list>
                </ul>
            </div>
            </#if>
        </div>
    </div>

    <div class="email-footer">
        <p>此邮件由系统自动发送，请勿直接回复。</p>
        <p style="margin-top: 8px;">© ${.now?string('yyyy')} IoT 物联网平台 · <a href="#">查看详情</a></p>
    </div>
</div>
</body>
</html>
