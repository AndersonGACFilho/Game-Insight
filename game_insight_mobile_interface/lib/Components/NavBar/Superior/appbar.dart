import 'package:flutter/material.dart';
import 'package:game_insight/Theme/themes.dart';

/// @brief The GameInsightAppBar widget.
/// @details This widget represents the custom AppBar used in the app.
/// It contains the logo and navigation icons for achievements and user profile.
class GameInsightAppBar extends StatelessWidget implements PreferredSizeWidget {
  @override
  Widget build(BuildContext context) {
    return AppBar(
      automaticallyImplyLeading: false,
      title: Container(
        child:
        const Image(
          image: AssetImage('assets/images/icons/logo/logo.png'),
          alignment: Alignment.center,
          width: 50,
          height: 60,
          fit: BoxFit.contain,
        ),
        margin: const EdgeInsets.only(left: 10),
      ),
      backgroundColor: AppTheme.navBarColor,
      actions: [
        IconButton(
          icon: const Image(
            image: AssetImage(
              'assets/images/icons/sup_navbar/trophy/trophy_unselected.png',
            ),
            alignment: Alignment.center,
          ),
          onPressed: () {},
        ),
        IconButton(
          icon: const Image(
            image: AssetImage('assets/images/icons/sup_navbar/user.png'),
            alignment: Alignment.center,
          ),
          onPressed: () {},
        ),
      ],
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
