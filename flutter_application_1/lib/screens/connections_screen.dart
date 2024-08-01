import 'package:flutter/material.dart';
import 'add_marketplace_screen.dart';

class ConnectionsScreen extends StatelessWidget {
  const ConnectionsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Conexões',
          style: TextStyle(color: Colors.white), // Define a cor do texto do título
        ),
        iconTheme: IconThemeData(color: Colors.white), // Define a cor do ícone
        leading: IconButton(
          icon: Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop(); // função para voltar para a tela anterior
          },
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const AddMarketplaceScreen()),
                );
              },
              child: const Icon(Icons.add),
            ),
            const SizedBox(height: 20),
            const Text('Sem Registros', style: TextStyle(fontSize: 18, color: Colors.grey)),
          ],
        ),
      ),
    );
  }
}
