import 'dart:convert';
import 'package:flutter/services.dart';

Future<Map<String, dynamic>> loadConfig() async {
  final String response = await rootBundle.loadString('assets/config/config.json');
  return json.decode(response);
}
