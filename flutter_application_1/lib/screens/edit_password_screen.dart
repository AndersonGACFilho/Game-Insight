import 'package:flutter/material.dart';

class EditPasswordScreen extends StatelessWidget {
  const EditPasswordScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        title: const Text(
          'Editar senha',
          style: TextStyle(color: Colors.white), // Define a cor do texto do título
        ),
        elevation: 0,
        iconTheme: IconThemeData(color: Colors.white), // Define a cor do ícone
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop(); // função para voltar para a tela anterior
          },
        ),
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const TextField(
              decoration: InputDecoration(
                labelText: 'Senha atual',
                labelStyle: TextStyle(color: Colors.white),
                filled: true,
                fillColor: Colors.white24,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(8.0)),
                ),
              ),
              style: TextStyle(color: Colors.white),
              obscureText: true,
            ),
            const SizedBox(height: 20),
            const TextField(
              decoration: InputDecoration(
                labelText: 'Senha nova',
                labelStyle: TextStyle(color: Colors.white),
                filled: true,
                fillColor: Colors.white24,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(8.0)),
                ),
              ),
              style: TextStyle(color: Colors.white),
              obscureText: true,
            ),
            const SizedBox(height: 20),
            const TextField(
              decoration: InputDecoration(
                labelText: 'Confirmar senha nova',
                labelStyle: TextStyle(color: Colors.white),
                filled: true,
                fillColor: Colors.white24,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(8.0)),
                ),
              ),
              style: TextStyle(color: Colors.white),
              obscureText: true,
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {},
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF0A4892),
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
              ),
              child: const Text('Salvar'),
            ),
          ],
        ),
      ),
    );
  }
}
