import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:flutter_application_1/main.dart';

void main() {
  testWidgets('Login screen test', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(MyApp());

    // Verificar se os campos de texto estão presentes.
    expect(find.byKey(Key('usernameField')), findsOneWidget);
    expect(find.byKey(Key('passwordField')), findsOneWidget);

    // Verificar se o botão de login está presente.
    expect(find.byKey(Key('loginButton')), findsOneWidget);

    // Verificar o texto no botão.
    expect(find.text('Entrar'), findsOneWidget);
  });
}
