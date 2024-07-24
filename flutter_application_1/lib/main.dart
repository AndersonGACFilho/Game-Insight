import 'package:flutter/material.dart';
import 'screens/login_screen.dart';
import 'screens/registration_screen.dart';
import 'screens/empty_marketplace_screen.dart';
import 'screens/marketplace_list_screen.dart';
import 'screens/profile_screen.dart';
import 'screens/edit_name_screen.dart';
import 'screens/edit_user_screen.dart';
import 'screens/edit_password_screen.dart';
import 'screens/empty_connections_screen.dart';
import 'screens/add_marketplace_screen.dart';
import 'screens/connected_marketplaces_screen.dart';
import 'screens/marketplace_details_screen.dart'; // Importe a tela MarketplaceDetailsScreen aqui

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Game Insight',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        scaffoldBackgroundColor: const Color(0xFF00152D), // Define a cor de fundo padrão para todas as telas
      ),
      initialRoute: '/login',
      routes: {
        '/login': (context) => const LoginScreen(),
        '/register': (context) => const RegistrationScreen(),
        '/marketplace': (context) => const EmptyMarketplaceScreen(),
        '/marketplace_list': (context) => const MarketplaceListScreen(),
        '/marketplace_details': (context) => const MarketplaceDetailsScreen(name: 'PlayStation® Network'), // Ajuste conforme necessário para a tela inicial de detalhes do marketplace
        '/profile': (context) => const ProfileScreen(),
        '/edit_name': (context) => const EditNameScreen(),
        '/edit_user': (context) => const EditUserScreen(),
        '/edit_password': (context) => const EditPasswordScreen(),
        '/connections': (context) => const EmptyConnectionsScreen(),
        '/add_marketplace': (context) => const AddMarketplaceScreen(),
        '/connected_marketplaces': (context) => const ConnectedMarketplacesScreen(),
      },
    );
  }
}
