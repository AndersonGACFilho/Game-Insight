import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:game_insight/Screens/home-screen.dart';
import 'package:game_insight/Screens/login-screen.dart';
import 'package:game_insight/Theme/themes.dart';

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
              builder: (context) => LoginScreen(),
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
  final bool isPassword;
  final bool isDate;
  final String? Function(String?)? validator;

  const TextFieldComponent({
    Key? key,
    required this.controller,
    required this.labelText,
    required this.icon,
    required this.helperText,
    this.isPassword = false,
    this.isDate = false,
    this.validator,
  }) : super(key: key);

  @override
  _TextFieldComponentState createState() => _TextFieldComponentState();
}

// TextFieldComponentState: Handles the state of the TextFieldComponent.
class _TextFieldComponentState extends State<TextFieldComponent> {
  bool _isObscured = true;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 20.0),
      child: TextFormField(
        controller: widget.controller,
        obscureText: widget.isPassword && _isObscured,
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
        onTap: widget.isDate
            ? () async {
          // Show date picker
          DateTime? pickedDate = await showDatePicker(
            context: context,
            initialDate: DateTime.now(),
            firstDate: DateTime(1900),
            lastDate: DateTime.now(),
          );

          if (pickedDate != null) {
            // Format the picked date in Brazilian format (DD/MM/YYYY)
            String formattedDate = DateFormat('dd/MM/yyyy').format(pickedDate);
            setState(() {
              // Set the formatted date in the controller
              widget.controller.text = formattedDate;
            });
          }
        }
            : null,
      ),
    );
  }
}

// AlreadyHaveAccount: Handles "Already have an account?" row.
class AlreadyHaveAccount extends StatelessWidget {
  const AlreadyHaveAccount({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 10.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Text('Já possui uma conta?', style: TextStyle(
              color: AppTheme.iconColor,
              fontSize: AppTheme.formFieldTextSize
          )),
          TextButton(
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => LoginScreen()),
              );
            },
            child: const Text(
              'Entrar',
              style: TextStyle(
                  color: AppTheme.registerButtonColor,
                  fontWeight: FontWeight.bold,
                  fontSize: AppTheme.appLabelSize
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// RegistrationScreen: Main screen with the registration form.
class RegistrationScreen extends StatefulWidget {
  const RegistrationScreen({super.key});

  static final TextEditingController userController = TextEditingController();
  static final TextEditingController emailController = TextEditingController();
  static final TextEditingController birthdateController = TextEditingController();
  static final TextEditingController passwordController = TextEditingController();
  static final TextEditingController confirmPasswordController = TextEditingController();

  @override
  _RegistrationScreenState createState() => _RegistrationScreenState();
}

// RegistrationScreenState: Handles the state of the RegistrationScreen.
class _RegistrationScreenState extends State<RegistrationScreen> {
  final _formKey = GlobalKey<FormState>();

  Future<void> _registerUser(BuildContext context) async {
    // Load the API configuration
    Map<String, dynamic> config = await loadConfig();

    final String userApiUrl = config['userApiUrl'];
    final String registerEndpoint = config['userApiEndpoints']['register'];

    final url = Uri.parse('$userApiUrl$registerEndpoint'); // Full API URL

    // Format birthdate into a proper string, assuming it’s in dd/MM/yyyy format
    final DateTime parsedDate = DateFormat('dd/MM/yyyy').parse(RegistrationScreen.birthdateController.text);
    final String formattedBirthdate = parsedDate.toIso8601String();  // Convert to ISO 8601 format

    final body = jsonEncode({
      "name": RegistrationScreen.userController.text,
      "email": RegistrationScreen.emailController.text,
      "password": RegistrationScreen.passwordController.text,
      "profile": {
        "birthdate": formattedBirthdate,  // Use formatted date
      }
    });

    try {
      final response = await http.post(
        url,
        headers: {
          'Content-Type': 'application/json',
        },
        body: body,
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        // Registration successful, navigate to the home screen
        Navigator.pushReplacement(
          context,
          MaterialPageRoute(
            builder: (context) => const LoginScreen(),
          ),
        );
      } else {
        // Registration failed, handle the error
        final errorResponse = jsonDecode(response.body);
        showDialog(
          context: context,
          builder: (context) {
            return AlertDialog(
              title: const Text('Registration Failed'),
              content: Text(errorResponse['message'] ?? 'An error occurred.'),
              actions: <Widget>[
                TextButton(
                  onPressed: () {
                    Navigator.of(context).pop();
                  },
                  child: const Text('OK'),
                ),
              ],
            );
          },
        );
      }
    } catch (e) {
      // Handle connection error
      showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text('Error'),
            content: Text('An error occurred: $e'),
            actions: <Widget>[
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text('OK'),
              ),
            ],
          );
        },
      );
    }
  }

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
                title: 'Cadastro',
                description: 'Insira seus dados para se cadastrar',
              ),
              TextFieldComponent(
                controller: RegistrationScreen.userController,
                labelText: 'Usuário',
                icon: Icons.person,
                helperText: 'Insira seu usuário',
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira seu usuário';
                  }
                  return null;
                },
              ),
              TextFieldComponent(
                controller: RegistrationScreen.birthdateController,
                labelText: 'Data de nascimento',
                icon: Icons.calendar_today,
                helperText: 'Insira sua data de nascimento',
                isDate: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira sua data de nascimento';
                  }

                  // Define the expected date format: DD/MM/YYYY (Brazilian format)
                  final dateFormat = DateFormat('dd/MM/yyyy');

                  // Try parsing the date in Brazilian format
                  DateTime? parsedDate;
                  try {
                    parsedDate = dateFormat.parseStrict(value);
                  } catch (e) {
                    return 'Formato de data inválido. Use DD/MM/AAAA';
                  }

                  // Check if the parsed date is before the current date
                  if (!parsedDate.isBefore(DateTime.now())) {
                    return 'A data de nascimento deve ser anterior à data atual';
                  }

                  return null;
                },
              ),
              TextFieldComponent(
                controller: RegistrationScreen.emailController,
                labelText: 'E-mail',
                icon: Icons.email,
                helperText: 'Insira seu e-mail',
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira seu e-mail';
                  }
                  // Check if the email is in the correct format
                  if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
                    return 'E-mail inválido';
                  }
                },
              ),
              TextFieldComponent(
                controller: RegistrationScreen.passwordController,
                labelText: 'Senha',
                icon: Icons.lock,
                helperText: 'Insira sua senha',
                isPassword: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, insira sua senha';
                  }
                  return null;
                },
              ),
              TextFieldComponent(
                controller: RegistrationScreen.confirmPasswordController,
                labelText: 'Confirme sua senha',
                icon: Icons.lock,
                helperText: 'Confirme sua senha',
                isPassword: true,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return 'Por favor, confirme sua senha';
                  }
                  if (value != RegistrationScreen.passwordController.text) {
                    return 'As senhas não coincidem';
                  }
                  return null;
                },
              ),
              RegistrationButton(
                formKey: _formKey,
              ),
              const AlreadyHaveAccount(),
            ],
          ),
        ),
      ),
    );
  }
}

// Update the `RegistrationButton` widget to trigger the `_registerUser` function
class RegistrationButton extends StatelessWidget {
  final GlobalKey<FormState> formKey; // Accept the formKey as a parameter

  const RegistrationButton({super.key, required this.formKey});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 50.0),
      child: ElevatedButton(
        onPressed: () {
          // Trigger validation
          if (formKey.currentState!.validate()) {
            _RegistrationScreenState()._registerUser(context);
          }
        },
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
          'Cadastrar',
          style: TextStyle(
            color: AppTheme.iconColor,
            fontSize: AppTheme.appLabelSize,
          ),
        ),
      ),
    );
  }
}
