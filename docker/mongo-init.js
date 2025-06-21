// Script de inicialização do MongoDB
// Este script é executado quando o container MongoDB é criado pela primeira vez

// Criar banco de dados
db = db.getSiblingDB('springboot_db');

// Criar usuário para a aplicação (opcional)
// db.createUser({
//   user: 'springboot_user',
//   pwd: 'springboot_password',
//   roles: [
//     {
//       role: 'readWrite',
//       db: 'springboot_db'
//     }
//   ]
// });

// Criar índices para melhor performance
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "active": 1 });
db.users.createIndex({ "name": "text" });

db.products.createIndex({ "name": "text" });
db.products.createIndex({ "category": 1 });
db.products.createIndex({ "available": 1 });
db.products.createIndex({ "price": 1 });

// Inserir dados de exemplo (opcional)
db.users.insertMany([
  {
    name: "João Silva",
    email: "joao@example.com",
    phone: "(11) 99999-9999",
    bio: "Desenvolvedor Java especialista em Spring Boot",
    active: true,
    createdDate: new Date(),
    createdBy: "system"
  },
  {
    name: "Maria Santos",
    email: "maria@example.com",
    phone: "(11) 88888-8888",
    bio: "Analista de sistemas com foco em APIs REST",
    active: true,
    createdDate: new Date(),
    createdBy: "system"
  }
]);

db.products.insertMany([
  {
    name: "Notebook Dell Inspiron",
    description: "Notebook para desenvolvimento com 16GB RAM e SSD 512GB",
    price: NumberDecimal("2999.99"),
    category: "Eletrônicos",
    tags: ["notebook", "dell", "desenvolvimento"],
    available: true,
    stock: 10,
    createdDate: new Date(),
    createdBy: "system"
  },
  {
    name: "Mouse Logitech MX Master",
    description: "Mouse ergonômico para produtividade",
    price: NumberDecimal("299.99"),
    category: "Acessórios",
    tags: ["mouse", "logitech", "ergonômico"],
    available: true,
    stock: 25,
    createdDate: new Date(),
    createdBy: "system"
  },
  {
    name: "Teclado Mecânico Keychron",
    description: "Teclado mecânico sem fio para programadores",
    price: NumberDecimal("599.99"),
    category: "Acessórios",
    tags: ["teclado", "mecânico", "keychron"],
    available: true,
    stock: 15,
    createdDate: new Date(),
    createdBy: "system"
  }
]);

print("Banco de dados inicializado com sucesso!");

