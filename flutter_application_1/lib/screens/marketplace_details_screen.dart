import 'package:flutter/material.dart';

class MarketplaceDetailsScreen extends StatelessWidget {
  final String name;

  const MarketplaceDetailsScreen({Key? key, required this.name}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        title: Text('Registro - Marketplaces'),
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Adicionar Marketplace',
              style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold, color: Colors.white),
            ),
            SizedBox(height: 20),
          
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
            SizedBox(height: 20),
            Center(
              child: ElevatedButton(
                onPressed: () {},
                child: Text('Cadastrar', style: TextStyle(color: Colors.white)),
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color.fromARGB(255, 7, 88, 155),
                  padding: EdgeInsets.symmetric(horizontal: 32, vertical: 12),
                ),
              ),
            ),
            Center(
              child: TextButton(
                onPressed: () {},
                child: Text(
                  'Já tem conta? Entre!',
                  style: TextStyle(color: Colors.white),
                ),
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
      margin: EdgeInsets.symmetric(vertical: 8.0),
      child: ListTile(
        leading: Image.asset(assetPath, width: 40, height: 40),
        title: Text(name, style: TextStyle(color: Colors.white)),
        subtitle: Text(subtitle, style: TextStyle(color: Colors.white70)),
        trailing: ElevatedButton(
          onPressed: () {},
          child: Text('Desconectar',style: TextStyle(color: Colors.white)),          
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.red,
            
          ),
        ),
      ),
    );
  }
}
