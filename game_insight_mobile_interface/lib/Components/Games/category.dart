import 'package:flutter/material.dart';

/// @brief The Category widget.
/// @details This class contains the Category widget.
/// The Category widget is a widget that displays the category of a game.
/// The category is displayed as a container with the name of the category.
/// The category is displayed at the bottom of the game element.
/// The category is displayed as a container with a background color and a
/// border.

class Category extends StatefulWidget {
  /// @brief The constructor of the Category widget.
  /// @details This constructor creates a Category widget with a key and a name.
  /// The key is used to identify the widget and the name is the name of the
  /// category.
  /// @param key The key of the widget.
  /// @param name The name of the category.
  /// @return Category The Category widget.
  const Category({Key? key, required this.name}) : super(key: key);

  /// @brief The name of the category.
  final String name;

  /// @brief The createState function of the Category widget.
  /// @details This function creates the state of the Category widget.
  /// @param None.
  /// @return _CategoryState The state of the Category widget.
  @override
  _CategoryState createState() => _CategoryState();
}

class _CategoryState extends State<Category> {
  /// @brief The build function of the _CategoryState widget.
  /// @details This function builds the _CategoryState widget with the category
  /// information.
  @override
  Widget build(BuildContext context) {
    // Get the name of the category
    final categoryName = widget.name;

    // Return the category
    return Container(
      padding: const EdgeInsets.all(8.0),
      margin: const EdgeInsets.only(right: 8.0),
      decoration: BoxDecoration(
        color: Colors.black.withOpacity(0.5),
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: Text(
        categoryName,
        style: const TextStyle(
          color: Colors.white,
          fontSize: 16.0,
        ),
        overflow: TextOverflow.ellipsis,
        maxLines: 1,
      ),
    );

  }
}