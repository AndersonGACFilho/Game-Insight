import 'package:flutter/material.dart';

class RegistrationScreen extends StatefulWidget {
  const RegistrationScreen({Key? key}) : super(key: key);

  @override
  _RegistrationScreenState createState() => _RegistrationScreenState();
}

class _RegistrationScreenState extends State<RegistrationScreen> {
  // Controladores para os campos de senha
  final TextEditingController _passwordController = TextEditingController();
  final TextEditingController _confirmPasswordController =
      TextEditingController();

  // Flags para controlar a visibilidade das senhas
  bool _obscurePassword = true;
  bool _obscureConfirmPassword = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.white),
          onPressed: () {
            Navigator.pushReplacementNamed(context, '/login');
          },
        ),
      ),
      backgroundColor: const Color(0xFF00152D),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: SingleChildScrollView(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Cadastro',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 32,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 20),
                const TextField(
                  decoration: InputDecoration(
                    labelText: 'Nome Completo',
                    labelStyle: TextStyle(color: Colors.white),
                    prefixIcon: Icon(Icons.person, color: Colors.white),
                    filled: true,
                    fillColor: Colors.white24,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.all(Radius.circular(8.0)),
                    ),
                  ),
                  style: TextStyle(color: Colors.white),
                ),
                const SizedBox(height: 20),
                const TextField(
                  decoration: InputDecoration(
                    labelText: 'Usuário',
                    labelStyle: TextStyle(color: Colors.white),
                    prefixIcon: Icon(Icons.person, color: Colors.white),
                    filled: true,
                    fillColor: Colors.white24,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.all(Radius.circular(8.0)),
                    ),
                  ),
                  style: TextStyle(color: Colors.white),
                ),
                const SizedBox(height: 20),
                const TextField(
                  decoration: InputDecoration(
                    labelText: 'Email',
                    labelStyle: TextStyle(color: Colors.white),
                    prefixIcon: Icon(Icons.email, color: Colors.white),
                    filled: true,
                    fillColor: Colors.white24,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.all(Radius.circular(8.0)),
                    ),
                  ),
                  style: TextStyle(color: Colors.white),
                ),
                const SizedBox(height: 20),
                TextField(
                  controller: _passwordController,
                  decoration: InputDecoration(
                    labelText: 'Senha',
                    labelStyle: TextStyle(color: Colors.white),
                    prefixIcon: Icon(Icons.lock, color: Colors.white),
                    filled: true,
                    fillColor: Colors.white24,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.all(Radius.circular(8.0)),
                    ),
                    suffixIcon: IconButton(
                      icon: Icon(
                        _obscurePassword ? Icons.visibility : Icons.visibility_off,
                        color: Colors.white,
                      ),
                      onPressed: () {
                        setState(() {
                          _obscurePassword = !_obscurePassword;
                        });
                      },
                    ),
                  ),
                  style: TextStyle(color: Colors.white),
                  obscureText: _obscurePassword,
                ),
                const SizedBox(height: 20),
                TextField(
                  controller: _confirmPasswordController,
                  decoration: InputDecoration(
                    labelText: 'Confirmar Senha',
                    labelStyle: TextStyle(color: Colors.white),
                    prefixIcon: Icon(Icons.lock, color: Colors.white),
                    filled: true,
                    fillColor: Colors.white24,
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.all(Radius.circular(8.0)),
                    ),
                    suffixIcon: IconButton(
                      icon: Icon(
                        _obscureConfirmPassword ? Icons.visibility : Icons.visibility_off,
                        color: Colors.white,
                      ),
                      onPressed: () {
                        setState(() {
                          _obscureConfirmPassword = !_obscureConfirmPassword;
                        });
                      },
                    ),
                  ),
                  style: TextStyle(color: Colors.white),
                  obscureText: _obscureConfirmPassword,
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: () {
                    Navigator.pushReplacementNamed(context, '/marketplace');
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green, // Cor de fundo
                    foregroundColor: Colors.white, // Cor do texto
                  ),
                  child: const Text('Adicionar Marketplace'),
                ),
                const SizedBox(height: 20),
                ElevatedButton(
                  onPressed: () {
                    // Implementar a funcionalidade de cadastro
                  },
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF0A4892), // Cor de fundo
                    foregroundColor: Colors.white, // Cor do texto
                  ),
                  child: const Text('Cadastrar'),
                ),
                const SizedBox(height: 20),
                TextButton(
                  onPressed: () {
                    Navigator.pushReplacementNamed(context, '/login');
                  },
                  child: const Text(
                    'Já tem conta? Entre!',
                    style: TextStyle(color: Colors.blue),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
