import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';

import 'category.dart';

/// @brief The GameElement widget.
class GameElement extends StatefulWidget {
  final String backgroundImg;
  final int id;
  final String name;
  final double width;

  const GameElement(
      Key? key, this.id, this.backgroundImg, this.name, this.width)
      : super(key: key);

  @override
  _GameElementState createState() => _GameElementState();
}

class _GameElementState extends State<GameElement> {
  @override
  Widget build(BuildContext context) {
    final gameName = widget.name;

    return Hero(
      tag: 'game-element-${widget.id}',
      child: Stack(
        alignment: AlignmentDirectional.bottomStart,
        children: <Widget>[
          ClipRect(
            child: Container(
              decoration: BoxDecoration(
                image: DecorationImage(
                  image: CachedNetworkImageProvider(widget.backgroundImg),
                  fit: BoxFit.cover,
                ),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.end,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Container(
                  margin: const EdgeInsets.all(8.0),
                  child: Text(
                    gameName,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 40.0,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
                Container(
                  margin: const EdgeInsets.all(8.0),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: List.generate(3, (index) {
                      return Flexible(
                        child: Container(
                          margin: const EdgeInsets.only(right: 8.0),
                          child: Category(
                            name: 'Category $index',
                            key: Key('category-$index'),
                          ),
                        ),
                      );
                    }),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
