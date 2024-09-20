import 'package:flutter/material.dart';
import 'package:game_insight/Screens/home-screen.dart';
import 'package:game_insight/Screens/login-screen.dart';
import 'package:game_insight/Screens/registration-screen.dart';

/// @brief The main function of the application.
/// @details This function is the entry point of the application. It runs the
/// application by calling the runApp function with the MyApp widget as an
/// argument.
/// @param None.
/// @return void.
void main() {
  runApp(const MyApp());
}

/// @brief The MyApp widget. \n
/// @details This widget is the root of the application. It creates a
/// MaterialApp widget with a title and a theme. The theme is set to use
/// Material 3.
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  /// @brief The build function of the MyApp widget. \n
  /// @details This function builds the MyApp widget by returning a MaterialApp
  /// widget with a title and a theme. The theme is set to use Material 3. \n
  /// @param context The build context of the widget. \n
  /// @return MaterialApp The MaterialApp widget.
  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      title: 'Game Insight',
      theme: ThemeData(
        primarySwatch: Colors.blueGrey,
      ),
      home:
        LoginScreen(),
    );
  }
}

