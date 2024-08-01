import 'package:flutter/material.dart';
import 'marketplace_details_screen.dart';

class MarketplaceListScreen extends StatelessWidget {
  const MarketplaceListScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Adicionar Marketplace'),
        backgroundColor: const Color(0xFF00152D),
        iconTheme: const IconThemeData(color: Colors.white), // Define a cor do ícone
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.of(context).pop(); // função para voltar para a tela anterior
          },
        ),
      ),
      body: Container(
        color: const Color(0xFF00152D),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'Adicionar Marketplace',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.white,
                ),
              ),
              const SizedBox(height: 20),
              Card(
                color: const Color(0xFF2C2C2C),
                child: ListTile(
                  leading: Image.asset(
                    'assets/imagens/playstation.png',
                    height: 40,
                  ),
                  title: const Text(
                    'PlayStation® Network',
                    style: TextStyle(color: Colors.white),
                  ),
                  trailing: const Icon(Icons.chevron_right, color: Colors.white),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => MarketplaceDetailsScreen(name: 'PlayStation® Network'),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 10),
              Card(
                color: const Color(0xFF2C2C2C),
                child: ListTile(
                  leading: Image.asset(
                    'assets/imagens/steam.png',
                    height: 40,
                  ),
                  title: const Text(
                    'Steam',
                    style: TextStyle(color: Colors.white),
                  ),
                  trailing: const Icon(Icons.chevron_right, color: Colors.white),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => MarketplaceDetailsScreen(name: 'Steam'),
                      ),
                    );
                  },
                ),
              ),
              const SizedBox(height: 10),
              Card(
                color: const Color(0xFF2C2C2C),
                child: ListTile(
                  leading: Image.asset(
                    'assets/imagens/box.png',
                    height: 40,
                  ),
                  title: const Text(
                    'XBOX Live',
                    style: TextStyle(color: Colors.white),
                  ),
                  trailing: const Icon(Icons.chevron_right, color: Colors.white),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => MarketplaceDetailsScreen(name: 'XBOX Live'),
                      ),
                    );
                  },
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
