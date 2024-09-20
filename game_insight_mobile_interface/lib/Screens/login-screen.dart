import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:game_insight/Screens/registration-screen.dart';
import 'package:http/http.dart' as http;
import 'package:game_insight/Screens/home-screen.dart';
import 'package:game_insight/Theme/themes.dart';
import 'package:flutter/services.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

import 'forgot-passoword-screen.dart';

// loadConfig: Loads the configuration file
Future<Map<String, dynamic>> loadConfig() async {
  final String response = await rootBundle.loadString('assets/config/config.json');
  return json.decode(response);
}

// LoginScreen: Handles the login screen
class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

// LoginScreen: Handles the login screen
class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  late Map<String, dynamic> config;

  @override
  void initState() {
    super.initState();
    loadConfig().then((cfg) {
      setState(() {
        config = cfg;
      });
    });
  }

  Future<void> _login(BuildContext context) async {
    // Check if the form is valid
    if (_formKey.currentState!.validate()) {
      final url = config['userApiUrl'] + config['userApiEndpoints']['login'];

      // Prepare the request body
      final body = json.encode({
        'email': emailController.text,
        'password': passwordController.text,
      });

      // Make the POST request
      final response = await http.post(
        Uri.parse(url),
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      if (response.statusCode == 200) {
        // Successful login, navigate to home screen
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(builder: (context) => const HomeScreen()),
        );
        // Save the token for future requests as a bearer token
        final token = json.decode(response.body)['token'];
        final storage = FlutterSecureStorage();
        await storage.write(key: 'token', value: token);

      } else {
        // Handle login failure (e.g., show a snackbar with an error message)
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Login failed. Please check your credentials.')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.appBackgroundColor,
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(8.0),
        child: Container(
          height: MediaQuery.sizeOf(context).height,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const LogoAndTitle(),
              Container(
                margin: const EdgeInsets.only(top: 40.0),
                child: Form(
                  key: _formKey,
                  child: Column(
                    children: [
                      TextFieldComponent(
                        controller: emailController,
                        labelText: 'E-mail',
                        icon: Icons.email,
                      ),
                      TextFieldComponent(
                        controller: passwordController,
                        labelText: 'Senha',
                        icon: Icons.lock,
                        isPassword: true,
                      ),
                      LoginButton(formKey: _formKey, loginFunction: _login),
                    ],
                  ),
                ),
              ),
              const ForgotPasswordButton(),
              const RegisterQuestion(),
            ],
          ),
        ),
      ),
    );
  }
}

// TextFieldComponent: Handles email and password input fields
class TextFieldComponent extends StatefulWidget {
  final TextEditingController controller;
  final String labelText;
  final IconData icon;
  final bool isPassword;

  const TextFieldComponent({
    super.key,
    required this.controller,
    required this.labelText,
    required this.icon,
    this.isPassword = false,
  });

  @override
  _TextFieldComponentState createState() => _TextFieldComponentState();
}

class _TextFieldComponentState extends State<TextFieldComponent> {
  bool _isObscured = true;

  @override
  void initState() {
    super.initState();
    _isObscured = widget.isPassword; // Initialize the state for password visibility
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 10.0),
      child: TextFormField(
        controller: widget.controller,
        obscureText: widget.isPassword && _isObscured,
        decoration: InputDecoration(
          prefixIcon: Icon(
            widget.icon,
            color: AppTheme.placeholderColor,
          ),
          labelText: widget.labelText,
          labelStyle: const TextStyle(
            color: AppTheme.placeholderColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.appLabelSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.none,
          ),
          helperText: 'Insira seu ${widget.labelText}',
          helperStyle: const TextStyle(
            color: AppTheme.hintColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.appHintSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.none,
          ),
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8.0),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8.0),
            borderSide: const BorderSide(
              color: Colors.transparent,
            ),
          ),
          fillColor: const Color.fromARGB(50, 255, 255, 255),
          filled: true,
          floatingLabelStyle: const TextStyle(
            color: AppTheme.iconColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.appLabelSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.none,
          ),
          // Show/hide password button
          suffixIcon: widget.isPassword
              ? IconButton(
            icon: Icon(
              _isObscured ? Icons.visibility : Icons.visibility_off,
              color: AppTheme.iconColor,
            ),
            onPressed: () {
              setState(() {
                _isObscured = !_isObscured;
              });
            },
          )
              : null,
          errorStyle: const TextStyle(
            color: AppTheme.errorColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.appHintSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.none,
          ),
        ),
        cursorColor: AppTheme.iconColor,
        style: const TextStyle(
          color: AppTheme.placeholderColor,
          fontWeight: FontWeight.normal,
          fontSize: AppTheme.formFieldTextSize,
          fontFamily: AppTheme.appFontFamily,
          decoration: TextDecoration.none,
        ),
        validator: (value) {
          if (value == null || value.isEmpty) {
            return 'Por favor, insira seu ${widget.labelText}';
          }
          return null;
        },
      ),
    );
  }
}

// LoginButton: Handles the "Entrar" button and triggers login
class LoginButton extends StatelessWidget {
  final GlobalKey<FormState> formKey;
  final Function loginFunction;

  const LoginButton({super.key, required this.formKey, required this.loginFunction});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 50.0),
      child: ElevatedButton(
        onPressed: () => loginFunction(context), // Pass context for snack bar
        style: ButtonStyle(
          backgroundColor: MaterialStateProperty.all<Color>(
            AppTheme.formButtonColor,
          ),
          shape: MaterialStateProperty.all<RoundedRectangleBorder>(
            RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8.0),
            ),
          ),
          fixedSize: MaterialStateProperty.all<Size>(
            Size(MediaQuery.sizeOf(context).width / 2, 50),
          ),
        ),
        child: const Text(
          'Entrar',
          style: TextStyle(
            color: AppTheme.iconColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.appLabelSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.none,
          ),
        ),
      ),
    );
  }
}

// LogoAndTitle: Handles the logo and title section
class LogoAndTitle extends StatelessWidget {
  const LogoAndTitle({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 40.0),
      width: MediaQuery.sizeOf(context).width,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image(
            image: const AssetImage('assets/images/icons/logo/logo.png'),
            width: (MediaQuery.sizeOf(context).height) / 7,
            height: (MediaQuery.sizeOf(context).height) / 7,
            fit: BoxFit.fitHeight,
          ),
          const SizedBox(width: 10), // Space between logo and text
          const Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                'Game',
                style: TextStyle(
                  color: AppTheme.iconColor,
                  fontWeight: FontWeight.w900,
                  fontSize: AppTheme.appTitleSize,
                  fontFamily: AppTheme.appFontFamily,
                  decoration: TextDecoration.none,
                ),
              ),
              Text(
                'Insight',
                style: TextStyle(
                  color: AppTheme.iconColor,
                  fontWeight: FontWeight.w900,
                  fontSize: AppTheme.appTitleSize,
                  fontFamily: AppTheme.appFontFamily,
                  decoration: TextDecoration.none,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

// ForgotPasswordButton: Handles the "Esqueci minha senha" button
class ForgotPasswordButton extends StatelessWidget {
  const ForgotPasswordButton({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 5.0),
      child: TextButton(
        onPressed: () {
          // Handle forgot password logic, change to the appropriate screen
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => ForgotPasswordScreen(),
              settings: const RouteSettings(
                name: 'ForgotPasswordScreen',
              ),
            ),
          );
        },
        style: ButtonStyle(
          overlayColor: MaterialStateProperty.all<Color>(Colors.transparent),
        ),
        child: const Text(
          'Esqueci minha senha',
          style: TextStyle(
            color: AppTheme.iconColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.formFieldTextSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.underline,
            decorationColor: AppTheme.iconColor,
          ),
        ),
      ),
    );
  }
}

// RegisterQuestion: Handles "Don't have an account? Register" section
class RegisterQuestion extends StatelessWidget {
  const RegisterQuestion({super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text(
          'Não possui uma conta?',
          style: TextStyle(
            color: AppTheme.iconColor,
            fontWeight: FontWeight.normal,
            fontSize: AppTheme.formFieldTextSize,
            fontFamily: AppTheme.appFontFamily,
            decoration: TextDecoration.none,
          ),
        ),
        TextButton(
          onPressed: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => const RegistrationScreen(),
                settings: const RouteSettings(
                  name: 'RegistrationScreen',
                ),
              ),
            );
          },
          style: ButtonStyle(
            overlayColor: MaterialStateProperty.all<Color>(Colors.transparent),
          ),
          child: const Text(
            'Cadastre-se',
            style: TextStyle(
              color: AppTheme.registerButtonColor,
              fontWeight: FontWeight.bold,
              fontSize: AppTheme.registerQuestionSize,
              fontFamily: AppTheme.appFontFamily,
            ),
          ),
        ),
      ],
    );
  }
}

