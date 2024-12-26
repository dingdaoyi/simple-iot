<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>设备事件通知</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f9f9f9;
            margin: 0;
            padding: 20px;
        }
        .email-container {
            background: #ffffff;
            border: 1px solid #ddd;
            border-radius: 8px;
            max-width: 600px;
            margin: auto;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .email-header {
            font-size: 18px;
            font-weight: bold;
            color: #2c3e50;
            margin-bottom: 10px;
        }
        .email-content {
            font-size: 14px;
        }
        .email-content strong {
            color: #e74c3c;
        }
        .event-content {
            margin-top: 15px;
            padding: 10px;
            background: #f2f2f2;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .event-content ul {
            list-style-type: none;
            padding: 0;
            margin: 0;
        }
        .event-content li {
            margin-bottom: 8px;
        }
        .event-content li:before {
            content: "• ";
            color: #3498db;
            font-weight: bold;
        }
        .email-footer {
            margin-top: 20px;
            font-size: 12px;
            color: #777;
        }
    </style>
</head>
<body>
<div class="email-container">
    <p class="email-header">设备事件通知</p>
    <div class="email-content">
        <p>尊敬的用户，</p>
        <p>您的设备 <strong>${deviceName}:${deviceKey}</strong> 于 <strong>${eventTime}</strong> 发生了
            <strong>${eventTypeName}</strong> 事件，请及时确认。</p>
        <div class="event-content">
            <p><strong>事件内容：</strong></p>
            <ul>
                <#list eventContent?keys as key>
                    <li>${key}: ${eventContent[key]}</li>
                </#list>
            </ul>
        </div>
        <p class="email-footer">此致，<br>系统管理员</p>
    </div>
</div>
</body>
</html>