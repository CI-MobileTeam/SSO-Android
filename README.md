# SSO-Android
kotlin ThirdPartyLogin setting  
step1:  
要自行到 facebook developer, google api console, line developer 綁定自己的packeage得到
facebook_app_id, fb_login_protocol_scheme, google_login_client_id, line_channel_id  
step2:  
local.properties 需要添加以下的值  
facebook_app_id='your facebook_app_id'  
fb_login_protocol_scheme='your fb_login_protocol_scheme'  
google_login_client_id='your google_login_client_id'  
line_channel_id='your line_channel_id'  
step3:  
project層gradle
allprojects {
    repositories {
        maven {
            url 'https://dl.bintray.com/cloudinteractive/ThirdPartyLogin'
        }
    }
}  
step4:  
dependencies {
  implementation 'com.cloud-interactive.mobileteam:sso-kotlin:1.0.1'
}  
step5:  
ThirdPartyLogin 使用方法:  
step1: 呼叫 ThirdPartyLoginManager.INSTANCE.setThirdPartyLogin() 設定登入方式  
step2: 呼叫 ThirdPartyLoginManager.INSTANCE.logIn() 進行登入
