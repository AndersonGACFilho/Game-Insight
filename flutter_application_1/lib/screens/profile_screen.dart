import 'package:flutter/material.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF00152D),
      appBar: AppBar(
        backgroundColor: const Color(0xFF00152D),
        title: const Text('Perfil'),
        elevation: 0,
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Column(
                children: [
                  CircleAvatar(
                    radius: 40,
                    backgroundColor: Colors.white24,
                    child: Icon(Icons.person, size: 40, color: Colors.white),
                  ),
                  const SizedBox(height: 10),
                  const Text(
                    'Nome Sobrenome',
                    style: TextStyle(color: Colors.white, fontSize: 24, fontWeight: FontWeight.bold),
                  ),
                  const Text(
                    'Usuário | Game Insight',
                    style: TextStyle(color: Colors.white54, fontSize: 16),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 30),
            ListTile(
              title: const Text('Nome', style: TextStyle(color: Colors.white)),
              trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white),
              onTap: () => Navigator.pushNamed(context, '/edit_name'),
            ),
            ListTile(
              title: const Text('Usuário', style: TextStyle(color: Colors.white)),
              trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white),
              onTap: () => Navigator.pushNamed(context, '/edit_user'),
            ),
            ListTile(
              title: const Text('Senha', style: TextStyle(color: Colors.white)),
              trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white),
              onTap: () => Navigator.pushNamed(context, '/edit_password'),
            ),
            ListTile(
              title: const Text('Foto de perfil', style: TextStyle(color: Colors.white)),
              trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white),
              onTap: () {},
            ),
            ListTile(
              title: const Text('Conexões', style: TextStyle(color: Colors.white)),
              trailing: const Icon(Icons.arrow_forward_ios, color: Colors.white),
              onTap: () => Navigator.pushNamed(context, '/connections'),
            ),
            ListTile(
              title: const Text('Sair', style: TextStyle(color: Colors.red)),
              trailing: const Icon(Icons.exit_to_app, color: Colors.red),
              onTap: () {
                Navigator.pushReplacementNamed(context, '/login');
              },
            ),
          ],
        ),
      ),
    );
  }
}
