//package com.example.escolafutebolapp.ui.activity
//
//import UserRepository
//import android.graphics.Typeface
//import android.os.Bundle
//import android.text.InputType
//import android.view.Gravity
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.isVisible
//import androidx.lifecycle.ViewModelProvider
//import com.example.escolafutebolapp.R
//import com.example.escolafutebolapp.viewmodel.ForgotPasswordUiState
//import com.example.escolafutebolapp.viewmodel.ForgotPasswordViewModel
//import com.google.firebase.firestore.FirebaseFirestore
//
//class ForgotPasswordActivity : AppCompatActivity() {
//
//    private lateinit var viewModel: ForgotPasswordViewModel
//
//    // Declaração dos componentes de UI
//    private lateinit var scrollView: ScrollView
//    private lateinit var mainLayout: LinearLayout
//    private lateinit var titleText: TextView
//    private lateinit var subtitleText: TextView
//    private lateinit var emailInput: EditText
//    private lateinit var emailLayout: LinearLayout
//    private lateinit var btnRequestReset: Button
//    private lateinit var tokenSection: LinearLayout
//    private lateinit var tokenInput: EditText
//    private lateinit var newPasswordInput: EditText
//    private lateinit var confirmPasswordInput: EditText
//    private lateinit var btnResetPassword: Button
//    private lateinit var btnBackToLogin: Button
//    private lateinit var progressBar: ProgressBar
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Cria a UI programaticamente
//        createUI()
//
//        // Configura o ViewModel
//        setupViewModel()
//
//        setupObservers()
//        setupClickListeners()
//    }
//
//    private fun createUI() {
//        // Configuração do ScrollView principal
//        scrollView = ScrollView(this).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//            setPadding(50, 30, 50, 30)
//        }
//
//        // Layout principal
//        mainLayout = LinearLayout(this).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            orientation = LinearLayout.VERTICAL
//        }
//
//        // Título
//        titleText = TextView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.CENTER
//                setMargins(0, 0, 0, 20)
//            }
//            text = "Recuperar Senha"
//            textSize = 24f
//            setTypeface(null, Typeface.BOLD)
//            setTextColor(resources.getColor(android.R.color.black, null))
//        }
//
//        // Subtítulo
//        subtitleText = TextView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.CENTER
//                setMargins(0, 0, 0, 50)
//            }
//            text = "Digite seu email para receber um código de verificação"
//            textSize = 14f
//            setTextColor(resources.getColor(android.R.color.darker_gray, null))
//            gravity = Gravity.CENTER
//        }
//
//        // Layout do email
//        emailLayout = LinearLayout(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 30)
//            }
//            orientation = LinearLayout.VERTICAL
//        }
//
//        // Label do email
//        val emailLabel = TextView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 10)
//            }
//            text = "Email"
//            textSize = 16f
//            setTextColor(resources.getColor(android.R.color.black, null))
//        }
//
//        // Input do email
//        emailInput = EditText(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            hint = "seu@email.com"
//            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//            setBackgroundResource(R.drawable.edit_text_background) // Você precisa criar este drawable
//            setPadding(20, 20, 20, 20)
//        }
//
//        // Botão de solicitar reset
//        btnRequestReset = Button(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 40)
//            }
//            text = "Enviar Código"
//            setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
//            setTextColor(resources.getColor(android.R.color.white, null))
//            textSize = 16f
//        }
//
//        // Seção do token (inicialmente invisível)
//        tokenSection = LinearLayout(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            orientation = LinearLayout.VERTICAL
//            visibility = View.GONE
//        }
//
//        // Título da seção do token
//        val tokenTitle = TextView(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 20)
//            }
//            text = "Verificação"
//            textSize = 18f
//            setTypeface(null, Typeface.BOLD)
//            setTextColor(resources.getColor(android.R.color.black, null))
//        }
//
//        // Input do token
//        tokenInput = EditText(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 20)
//            }
//            hint = "Código de 6 dígitos"
//            inputType = InputType.TYPE_CLASS_NUMBER
//            maxLines = 1
//            setBackgroundResource(R.drawable.edit_text_background)
//            setPadding(20, 20, 20, 20)
//        }
//
//        // Input da nova senha
//        newPasswordInput = EditText(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 20)
//            }
//            hint = "Nova Senha (mínimo 6 caracteres)"
//            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
//            maxLines = 1
//            setBackgroundResource(R.drawable.edit_text_background)
//            setPadding(20, 20, 20, 20)
//        }
//
//        // Input de confirmação de senha
//        confirmPasswordInput = EditText(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 0, 0, 20)
//            }
//            hint = "Confirmar Nova Senha"
//            inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
//            maxLines = 1
//            setBackgroundResource(R.drawable.edit_text_background)
//            setPadding(20, 20, 20, 20)
//        }
//
//        // Botão de redefinir senha
//        btnResetPassword = Button(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//            text = "Redefinir Senha"
//            setBackgroundColor(resources.getColor(R.color.colorPrimary, null))
//            setTextColor(resources.getColor(android.R.color.white, null))
//            textSize = 16f
//            visibility = View.GONE
//        }
//
//        // Botão voltar para login
//        btnBackToLogin = Button(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(0, 30, 0, 0)
//            }
//            text = "Voltar para Login"
//            setBackgroundColor(resources.getColor(android.R.color.transparent, null))
//            setTextColor(resources.getColor(R.color.colorPrimary, null))
//            textSize = 14f
//            visibility = View.GONE
//        }
//
//        // ProgressBar
//        progressBar = ProgressBar(this).apply {
//            layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//                gravity = Gravity.CENTER
//                setMargins(0, 30, 0, 0)
//            }
//            visibility = View.GONE
//        }
//
//        // Montagem da UI
//        emailLayout.addView(emailLabel)
//        emailLayout.addView(emailInput)
//
//        tokenSection.addView(tokenTitle)
//        tokenSection.addView(tokenInput)
//        tokenSection.addView(newPasswordInput)
//        tokenSection.addView(confirmPasswordInput)
//        tokenSection.addView(btnResetPassword)
//
//        mainLayout.addView(titleText)
//        mainLayout.addView(subtitleText)
//        mainLayout.addView(emailLayout)
//        mainLayout.addView(btnRequestReset)
//        mainLayout.addView(tokenSection)
//        mainLayout.addView(btnBackToLogin)
//        mainLayout.addView(progressBar)
//
//        scrollView.addView(mainLayout)
//        setContentView(scrollView)
//    }
//
//    private fun setupViewModel() {
//        val factory = ForgotPasswordViewModelFactory(
//            UserRepository(FirebaseFirestore.getInstance()),
//            EmailService()
//        )
//        viewModel = ViewModelProvider(this, factory)[ForgotPasswordViewModel::class.java]
//    }
//
//    private fun setupObservers() {
//        viewModel.uiState.observe(this) { state ->
//            handleUiState(state)
//        }
//
//        viewModel.navigateToLogin.observe(this) { shouldNavigate ->
//            if (shouldNavigate) {
//                navigateToLogin()
//                viewModel.resetNavigation()
//            }
//        }
//    }
//
//    private fun handleUiState(state: ForgotPasswordUiState) {
//        when (state) {
//            is ForgotPasswordUiState.Idle -> {
//                hideLoading()
//            }
//            is ForgotPasswordUiState.Loading -> {
//                showLoading()
//            }
//            is ForgotPasswordUiState.Success -> {
//                hideLoading()
//                showSuccess(state.message)
//                emailInput.isEnabled = false
//                btnRequestReset.isEnabled = false
//                showTokenInputSection()
//            }
//            is ForgotPasswordUiState.PasswordResetSuccess -> {
//                hideLoading()
//                showSuccess(state.message)
//                btnResetPassword.isEnabled = false
//                showLoginButton()
//            }
//            is ForgotPasswordUiState.Error -> {
//                hideLoading()
//                showError(state.message)
//            }
//        }
//    }
//
//    private fun setupClickListeners() {
//        btnRequestReset.setOnClickListener {
//            val email = emailInput.text.toString().trim()
//            if (email.isNotEmpty()) {
//                viewModel.requestPasswordReset(email)
//            } else {
//                showError("Por favor, insira seu email")
//            }
//        }
//
//        btnResetPassword.setOnClickListener {
//            val email = emailInput.text.toString().trim()
//            val token = tokenInput.text.toString().trim()
//            val newPassword = newPasswordInput.text.toString().trim()
//            val confirmPassword = confirmPasswordInput.text.toString().trim()
//
//            if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
//                showError("Por favor, preencha todos os campos")
//                return@setOnClickListener
//            }
//
//            if (newPassword != confirmPassword) {
//                showError("As senhas não coincidem")
//                return@setOnClickListener
//            }
//
//            if (newPassword.length < 6) {
//                showError("A senha deve ter pelo menos 6 caracteres")
//                return@setOnClickListener
//            }
//
//            viewModel.resetPassword(email, token, newPassword)
//        }
//
//        btnBackToLogin.setOnClickListener {
//            viewModel.navigateToLoginScreen()
//        }
//    }
//
//    private fun showLoading() {
//        progressBar.isVisible = true
//        btnRequestReset.isEnabled = false
//        btnResetPassword.isEnabled = false
//    }
//
//    private fun hideLoading() {
//        progressBar.isVisible = false
//        btnRequestReset.isEnabled = true
//        btnResetPassword.isEnabled = true
//    }
//
//    private fun showSuccess(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
//
//    private fun showError(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
//
//    private fun showTokenInputSection() {
//        tokenSection.visibility = View.VISIBLE
//        btnResetPassword.visibility = View.VISIBLE
//        tokenInput.requestFocus()
//    }
//
//    private fun showLoginButton() {
//        btnBackToLogin.visibility = View.VISIBLE
//    }
//
//    private fun navigateToLogin() {
//        finish()
//    }
//
//    override fun onBackPressed() {
//        if (tokenSection.isVisible) {
//            // Se já está na seção do token, volta para o início
//            tokenSection.visibility = View.GONE
//            btnResetPassword.visibility = View.GONE
//            btnBackToLogin.visibility = View.GONE
//            emailInput.isEnabled = true
//            btnRequestReset.isEnabled = true
//        } else {
//            super.onBackPressed()
//        }
//    }
//}