import 'package:flutter/material.dart';

class AddMarketplaceScreen extends StatelessWidget {
  const AddMarketplaceScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        title: const Text('Adicionar Marketplace'),
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
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
            const SizedBox(height: 20),
            Center(
              child: ElevatedButton(
                onPressed: () {},
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF0A4892),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                ),
                child: const Text('Cadastrar'),
              ),
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
