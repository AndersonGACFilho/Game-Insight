import 'package:flutter/material.dart';
import 'marketplace_list_screen.dart'; // Importa a tela MarketplaceListScreen

class EmptyConnectionsScreen extends StatelessWidget {
  const EmptyConnectionsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        title: const Text('Conexões'),
        elevation: 0,
        iconTheme: const IconThemeData(color: Colors.white), // Define a cor do ícone
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop(); // função para voltar para a tela anterior
          },
        ),
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'Sem Registros',
                style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => const MarketplaceListScreen()),
                  );
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF0A4892),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                ),
                child: const Text('Adicionar Marketplace'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
