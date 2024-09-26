import 'package:flutter/material.dart';
import 'package:game_insight/Theme/themes.dart';
import 'package:game_insight/Screens/login-screen.dart';

// BackButtonWidget: Handles the back button.
class BackButtonWidget extends StatelessWidget {
  const BackButtonWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 50.0),
      alignment: Alignment.topLeft,
      child: IconButton(
        icon: const Icon(Icons.arrow_back),
        onPressed: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const LoginScreen(),
            ),
          );
        },
        iconSize: AppTheme.appTitleSize,
        color: AppTheme.iconColor,
      ),
    );
  }
}

// TitleAndDescription: Handles the title and description text.
class TitleAndDescription extends StatelessWidget {
  final String title;
  final String description;

  const TitleAndDescription({
    Key? key,
    required this.title,
    required this.description,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            color: AppTheme.iconColor,
            fontSize: AppTheme.appTitleSize,
            fontWeight: FontWeight.w500,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          description,
          style: const TextStyle(
            color: AppTheme.iconColor,
            fontSize: AppTheme.appLabelSize,
            fontWeight: FontWeight.w300,
          ),
        ),
      ],
    );
  }
}

// TextFieldComponent: Reusable text field widget.
class TextFieldComponent extends StatefulWidget {
  final TextEditingController controller;
  final String labelText;
  final IconData icon;
  final String helperText;
  final String? Function(String?)? validator;

  const TextFieldComponent({
    Key? key,
    required this.controller,
    required this.labelText,
    required this.icon,
    required this.helperText,
    this.validator,
  }) : super(key: key);

  @override
  _TextFieldComponentState createState() => _TextFieldComponentState();
}

class _TextFieldComponentState extends State<TextFieldComponent> {
  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 20.0),
      child: TextFormField(
        controller: widget.controller,
        decoration: InputDecoration(
          prefixIcon: Icon(widget.icon, color: AppTheme.placeholderColor),
          labelText: widget.labelText,
          labelStyle: const TextStyle(
            color: AppTheme.placeholderColor,
            fontSize: AppTheme.appLabelSize,
            fontFamily: AppTheme.appFontFamily,
            fontWeight: FontWeight.normal,
          ),
          helperText: widget.helperText,
          helperStyle: const TextStyle(
            color: AppTheme.hintColor,
            fontSize: AppTheme.appHintSize,
            fontFamily: AppTheme.appFontFamily,
            fontWeight: FontWeight.normal,
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
          fillColor: AppTheme.formFieldColor,
          filled: true,
          floatingLabelStyle: const TextStyle(
            color: AppTheme.iconColor,
            fontSize: AppTheme.appLabelSize,
            fontFamily: AppTheme.appFontFamily,
            fontWeight: FontWeight.normal,
          ),
          errorStyle: const TextStyle(
            color: AppTheme.errorColor,
            fontSize: AppTheme.appHintSize,
            fontFamily: AppTheme.appFontFamily,
            fontWeight: FontWeight.normal,
          ),
        ),
        cursorColor: AppTheme.iconColor,
        style: const TextStyle(
          color: AppTheme.iconColor,
          fontSize: AppTheme.formFieldTextSize,
          fontFamily: AppTheme.appFontFamily,
          fontWeight: FontWeight.normal,
        ),
        validator: widget.validator,
      ),
    );
  }
}

// ResetPasswordButton: Handles the "Send Reset Link" button.
class ResetPasswordButton extends StatelessWidget {
  final GlobalKey<FormState> formKey;
  final TextEditingController emailController;

  const ResetPasswordButton({super.key, required this.formKey,
    required this.emailController});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 50.0),
      child: ElevatedButton(
        onPressed: () {
          if (formKey.currentState!.validate()) {
            // Add your logic to handle sending the reset password email
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text('Password reset link sent to ${
                  emailController.text}')),
            );
          }
        },
        style: ButtonStyle(
          backgroundColor: MaterialStateProperty.all<Color>(AppTheme.
            formButtonColor),
          shape: MaterialStateProperty.all<RoundedRectangleBorder>(
            RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(8.0),
            ),
          ),
          padding: MaterialStateProperty.all<EdgeInsetsGeometry>(
            const EdgeInsets.all(10.0),
          ),
        ),
        child: const Text(
          'Send Reset Link',
          style: TextStyle(
            color: AppTheme.iconColor,
            fontSize: AppTheme.appLabelSize,
          ),
        ),
      ),
    );
  }
}

// ForgotPasswordScreen: Main screen for password reset.
class ForgotPasswordScreen extends StatefulWidget {
  const ForgotPasswordScreen({super.key});

  @override
  _ForgotPasswordScreenState createState() => _ForgotPasswordScreenState();
}

class _ForgotPasswordScreenState extends State<ForgotPasswordScreen> {
  final TextEditingController emailController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppTheme.appBackgroundColor,
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(8.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              const BackButtonWidget(),
              const TitleAndDescription(
                title: 'Esqueceu sua senha?',
                description: 'Insira seu e-mail para receber um link de '
                    'redefinição de senha.',
              ),
              TextFieldComponent(
                controller: emailController,
                labelText: 'E-mail',
                icon: Icons.email,
                helperText: 'Insira seu e-mail',
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira seu e-mail';
                  }
                  if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').
                    hasMatch(value)) {
                    return 'E-mail inválido';
                  }
                  return null;
                },
              ),
              ResetPasswordButton(
                formKey: _formKey,
                emailController: emailController,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
