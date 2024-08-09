db = db.getSiblingDB('userdb');

// Criação da coleção 'users'
db.createCollection('users');

// Inserir dados iniciais na coleção 'users' (opcional)
db.users.insertMany([
    { name: "John Doe", email: "john.doe@example.com", password: "hashed_password" },
    { name: "Jane Doe", email: "jane.doe@example.com", password: "hashed_password" }
]);
