import 'package:flutter/material.dart';

import '../Games/game-element.dart';

class TopTenList extends StatelessWidget {
  TopTenList({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(8.0),
      child: Container(
        margin: const EdgeInsets.only(top: 20.0),
        child: Column(
          children: [
            ConstrainedBox(
              constraints: BoxConstraints(
                maxWidth: MediaQuery.sizeOf(context).width-20,
                maxHeight: 600,
                ),
              child: CarouselView(
                scrollDirection: Axis.horizontal,
                itemExtent: MediaQuery.sizeOf(context).width-20,
                itemSnapping: true,
                children:
                  List.generate(10, (index) {
                    return GameElement(
                      Key('game-element-$index'),
                      index,
                      'https://picsum.photos/200/300',
                      'Game $index',
                      MediaQuery.sizeOf(context).width-20,
                    );
                  }),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
