import 'package:flutter/material.dart';
import 'package:game_insight/Components/HomePage/top-ten-list.dart';
import 'package:game_insight/Components/NavBar/Superior/appbar.dart';
import 'package:game_insight/Theme/themes.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<StatefulWidget> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: GameInsightAppBar(),

      body: Column(
        children: [
          Expanded(
            child: TopTenList(),  // Expand para ocupar o espaço disponível
          ),
        ],
      ),
      backgroundColor: AppTheme.appBackgroundColor,
    );
  }
}