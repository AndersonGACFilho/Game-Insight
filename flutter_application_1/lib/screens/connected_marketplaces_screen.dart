import 'package:flutter/material.dart';

class ConnectedMarketplacesScreen extends StatelessWidget {
  const ConnectedMarketplacesScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        title: const Text(
          'Conexões',
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
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Conexões',
              style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold, color: Colors.white),
            ),
            const SizedBox(height: 20),
            _buildMarketplaceTile(
              context,
              'PlayStation® Network',
              'assets/imagens/playstation.png',
              'Sua conta está conectada à PlayStation® Network',
            ),
            _buildMarketplaceTile(
              context,
              'Steam',
              'assets/imagens/steam.png',
              'Sua conta está conectada à Steam',
            ),
            _buildMarketplaceTile(
              context,
              'XBOX Live',
              'assets/imagens/box.png',
              'Sua conta está conectada à XBOX Live',
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMarketplaceTile(BuildContext context, String name, String assetPath, String subtitle) {
    return Card(
      color: Colors.grey[900],
      margin: const EdgeInsets.symmetric(vertical: 8.0),
      child: ListTile(
        leading: Image.asset(assetPath, width: 40, height: 40),
        title: Text(name, style: const TextStyle(color: Colors.white)),
        subtitle: Text(subtitle, style: const TextStyle(color: Colors.white70)),
        trailing: ElevatedButton(
          onPressed: () {},
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.red,
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          ),
          child: const Text('Desconectar', style: TextStyle(color: Colors.white)),
        ),
      ),
    );
  }
}
