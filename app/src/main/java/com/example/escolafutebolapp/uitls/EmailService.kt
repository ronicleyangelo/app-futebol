package com.example.escolafutebolapp.service

import android.content.Context
import java.util.Calendar
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService(private val context: Context) {

    private val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", getProperty("SMTP_SERVER", "smtp.gmail.com"))
        put("mail.smtp.port", getProperty("SMTP_PORT", "587"))
        put("mail.smtp.ssl.trust", getProperty("SMTP_SERVER", "smtp.gmail.com"))
    }

    private val username = getProperty("EMAIL_SENDER", "")
    private val password = getProperty("EMAIL_PASSWORD", "")

    private fun getProperty(key: String, defaultValue: String): String {
        return try {
            val properties = java.util.Properties()
            context.assets.open("local.properties").use { input ->
                properties.load(input)
            }
            properties.getProperty(key, defaultValue)
        } catch (e: Exception) {
            defaultValue
        }
    }

    private val session: Session by lazy {
        Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })
    }

    fun sendPasswordResetEmail(toEmail: String, resetToken: String) {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username, "Escola Futebol Jacareí"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail))
                subject = "Recuperação de Senha - Escola Futebol Jacareí"
                setContent(createEmailTemplate(resetToken), "text/html; charset=utf-8")
            }

            Thread {
                try {
                    Transport.send(message)
                    android.util.Log.d("EmailService", "Email enviado com sucesso para: $toEmail")
                } catch (e: Exception) {
                    android.util.Log.e("EmailService", "Erro ao enviar email: ${e.message}", e)
                    e.printStackTrace()
                }
            }.start()

        } catch (e: Exception) {
            android.util.Log.e("EmailService", "Erro ao preparar email: ${e.message}", e)
            throw Exception("Erro ao enviar email: ${e.message}")
        }
    }

    private fun createEmailTemplate(token: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }
                .container { max-width: 600px; margin: 20px auto; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
                .header { background: linear-gradient(135deg, #FF6B35 0%, #F7931E 100%); padding: 30px; text-align: center; color: white; }
                .content { padding: 30px; }
                .token { background: #f8f9fa; padding: 25px; text-align: center; font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #FF6B35; border: 2px dashed #dee2e6; border-radius: 10px; margin: 25px 0; font-family: 'Courier New', monospace; }
                .footer { text-align: center; margin-top: 30px; font-size: 12px; color: #6c757d; padding: 20px; border-top: 1px solid #dee2e6; }
                .warning { background: #fff3cd; border: 1px solid #ffc107; padding: 20px; border-radius: 8px; margin: 25px 0; color: #856404; }
                .button { display: inline-block; padding: 12px 30px; background: #FF6B35; color: white; text-decoration: none; border-radius: 5px; margin: 10px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>⚽ Recuperação de Senha</h1>
                    <p>Escola de Futebol Jacareí</p>
                </div>
                <div class="content">
                    <p>Olá,</p>
                    <p>Recebemos uma solicitação para redefinir a senha da sua conta. Use o código de verificação abaixo para continuar com o processo:</p>
                    
                    <div class="token">$token</div>
                    
                    <p><strong>Insira este código no aplicativo para redefinir sua senha.</strong></p>
                    
                    <div class="warning">
                        <strong>⚠️ Importante:</strong> 
                        <ul>
                            <li>Este código expira em <strong>15 minutos</strong></li>
                            <li>Se você não solicitou esta redefinição, ignore este email</li>
                            <li>Nunca compartilhe este código com ninguém</li>
                            <li>Nossa equipe nunca pedirá sua senha ou código por email</li>
                        </ul>
                    </div>
                    
                    <p>Se você tiver alguma dúvida ou precisar de ajuda, entre em contato com nosso suporte.</p>
                    
                    <p>Atenciosamente,<br><strong>Equipe Escola de Futebol Jacareí</strong></p>
                </div>
                <div class="footer">
                    <p>© ${Calendar.getInstance().get(Calendar.YEAR)} Escola de Futebol Jacareí. Todos os direitos reservados.</p>
                    <p>Este é um email automático, por favor não responda.</p>
                </div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}