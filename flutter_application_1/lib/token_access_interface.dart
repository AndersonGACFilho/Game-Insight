import 'package:flutter_secure_storage/flutter_secure_storage.dart';

// Create storage
final storage = FlutterSecureStorage();

// Store token
Future<void> storeToken(String token) async {
  await storage.write(key: 'token', value: token);
}

// Retrieve token
Future<String?> getToken() async {
  return await storage.read(key: 'token');
}

// Remove token
Future<void> removeToken() async {
  await storage.delete(key: 'token');
}
