import 'package:flutter/material.dart';

class EmptyMarketplaceScreen extends StatelessWidget {
  const EmptyMarketplaceScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      body: SafeArea(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Adicionar Marketplace',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 24, // Tamanho do título
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 20),
                const Icon(
                  Icons.add_circle,
                  color: Colors.green,
                  size: 100,
                ),
                const SizedBox(height: 20),
                const Text(
                  'Sem Registros',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 24,
                  ),
                ),
                const SizedBox(height: 20),
                const Text(
                  'Sem Registra suas contas para poder visualizar suas informações em um único lugar',
                  style: TextStyle(
                    color: Colors.grey,
                    fontSize: 15,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 50),
                ElevatedButton(
                  onPressed: () {
                    Navigator.pushReplacementNamed(context, '/marketplace_list');
                  },
                  style: ElevatedButton.styleFrom(
                    padding: EdgeInsets.symmetric(vertical: 20, horizontal: 40), // Ajuste de padding para aumentar o tamanho
                    backgroundColor: Colors.blue, // Cor de fundo
                    foregroundColor: Colors.white, // Cor do texto
                  ),
                  child: const Text(
                    'Cadastrar',
                    style: TextStyle(fontSize: 18), // Ajuste de tamanho do texto
                  ),
                ),
                const SizedBox(height: 20),
                Container(
                  margin: const EdgeInsets.only(bottom: 20), // Margem de 20 pixels da borda inferior
                  child: TextButton(
                    onPressed: () {
                      Navigator.pushReplacementNamed(context, '/login');
                    },
                    child: const Text(
                      'Já tem conta? Entre!',
                      style: TextStyle(color: Colors.blue),
                    ),
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
