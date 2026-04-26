# 🏗️ Ferretería Rodamientos y Fierros - App CRUD Android

Una aplicación Android completa para gestionar las operaciones de una ferretería. Implementa CRUD (Create, Read, Update, Delete) para clientes, productos, pedidos y facturas con una base de datos SQLite normalizada y una interfaz de usuario intuitiva.

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white)

**[Características](#-características) • [Instalación](#-instalación) • [Uso](#-uso)**

</div>

---

## 📱 Características

### ✅ Gestión de Clientes
- Crear nuevos clientes con nombre, dirección y teléfono
- Ver lista de todos los clientes
- Editar información existente
- Eliminar clientes
- Buscar clientes por nombre en tiempo real

### ✅ Gestión de Productos
- Agregar productos con fabricante y valor
- Consultar catálogo completo
- Editar datos de productos
- Eliminar productos del sistema
- Buscar por fabricante
- Filtrar por rango de precios

### ✅ Gestión de Pedidos
- Crear pedidos asociados a clientes
- Ver lista de pedidos con detalles
- Editar descripción de pedidos
- Eliminar pedidos (con cascada)
- Visualizar productos de cada pedido
- Calcular total de pedidos automáticamente

### ✅ Gestión de Facturas
- Generar facturas desde pedidos
- Visualizar todas las facturas
- Ver detalles completos con productos y cliente
- Eliminar facturas
- **Calcular ingresos totales en tiempo real**

### ✨ Características Adicionales
- **Base de datos normalizada** (3ª Forma Normal)
- **Integridad referencial** mediante Foreign Keys
- **Búsqueda en tiempo real** con TextWatcher
- **Interfaz moderna** con Material Design
- **Validación de datos** antes de guardar

---

## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología |
|-----------|-----------|
| **Lenguaje** | Kotlin |
| **Framework** | Android Jetpack |
| **Base de Datos** | SQLite |
| **UI Components** | RecyclerView, AlertDialog, LinearLayout |
| **Patrón de Arquitectura** | Repository Pattern |
| **Gestor de Dependencias** | Gradle |
| **Target SDK** | Android 34 |
| **Min SDK** | Android 21 (API 5.0) |

---

## 📋 Requisitos Previos

- Android Studio 4.0 o superior
- Java 8 o superior
- Android SDK 21+
- Kotlin 1.5+
- Gradle 7.0+

---

## 📥 Instalación

### 1. Clonar el Repositorio
```bash
git clone https://github.com/tuusuario/ferreteria-crud-android.git
cd ferreteria-crud-android
```

### 2. Abrir en Android Studio
```bash
# Abre Android Studio
File → Open → Selecciona la carpeta del proyecto
```

### 3. Sincronizar Gradle
```
Android Studio detectará y sincronizará automáticamente
o presiona: Ctrl + Shift + R (Windows/Linux) o Cmd + Shift + R (Mac)
```

### 4. Crear Estructura de Carpetas
```
src/main/java/com/ferreteria/app/
├── database/
│   └── DatabaseHelper.kt
├── models/
│   └── Models.kt
├── repository/
│   ├── ClienteRepository.kt
│   ├── ProductoRepository.kt
│   ├── PedidoRepository.kt
│   ├── PedidoProductoRepository.kt
│   └── FacturaRepository.kt
├── ui/
│   ├── MainActivity.kt
│   ├── ClientesActivity.kt
│   ├── ProductosActivity.kt
│   ├── PedidosActivity.kt
│   └── FacturasActivity.kt
└── adapters/
    ├── ClienteAdapter.kt
    ├── ProductoAdapter.kt
    ├── PedidoAdapter.kt
    └── FacturaAdapter.kt
```

### 5. Compilar y Ejecutar
```bash
# Compilar
Build → Rebuild Project

# Ejecutar en emulador o dispositivo
Run → Run 'app'
```

---

## 🚀 Uso Rápido

### Agregar un Cliente
```
1. Toca "Gestionar Clientes" en el menú principal
2. Presiona "+ Agregar Nuevo Cliente"
3. Ingresa: Nombre, Dirección, Teléfono
4. Toca "Guardar"
```

### Crear un Pedido
```
1. Toca "Gestionar Pedidos"
2. Presiona "+ Nuevo Pedido"
3. Ingresa: ID del Cliente, Descripción
4. Toca "Guardar"
5. La fecha se genera automáticamente
```

### Generar una Factura
```
1. Toca "Gestionar Facturas"
2. Presiona "+ Generar Nueva Factura"
3. Ingresa: ID del Pedido
4. Toca "Generar"
5. El valor total se calcula automáticamente
6. Visualiza ingresos totales en tiempo real
```

### Buscar Información
```
• Clientes: Escribe el nombre en el campo de búsqueda
• Productos: Escribe el fabricante
• Búsqueda en tiempo real mientras escribes
```

### Tablas y Columnas

#### Clientes
```sql
CREATE TABLE Clientes (
    IdCliente INTEGER PRIMARY KEY AUTOINCREMENT,
    Nombre TEXT NOT NULL,
    Dirección TEXT,
    Teléfono TEXT
);
```

#### Productos
```sql
CREATE TABLE Productos (
    IdProducto INTEGER PRIMARY KEY AUTOINCREMENT,
    Fabricante TEXT,
    Valor REAL
);
```

#### Pedidos
```sql
CREATE TABLE Pedidos (
    IdPedido INTEGER PRIMARY KEY AUTOINCREMENT,
    IdCliente INTEGER NOT NULL,
    Descripción TEXT,
    Fecha TEXT,
    FOREIGN KEY (IdCliente) REFERENCES Clientes(IdCliente)
);
```

#### PedidoProductos (Relación N:M)
```sql
CREATE TABLE PedidoProductos (
    Id INTEGER PRIMARY KEY AUTOINCREMENT,
    IdPedido INTEGER NOT NULL,
    IdProducto INTEGER NOT NULL,
    Cantidad INTEGER,
    FOREIGN KEY (IdPedido) REFERENCES Pedidos(IdPedido),
    FOREIGN KEY (IdProducto) REFERENCES Productos(IdProducto),
    UNIQUE(IdPedido, IdProducto)
);
```

#### Facturas
```sql
CREATE TABLE Facturas (
    IdFactura INTEGER PRIMARY KEY AUTOINCREMENT,
    IdPedido INTEGER NOT NULL UNIQUE,
    Fecha TEXT,
    ValorTotal REAL,
    FOREIGN KEY (IdPedido) REFERENCES Pedidos(IdPedido)
);
```

---

## 🏗️ Estructura del Proyecto

### Arquitectura: Repository Pattern

```
┌─────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN (UI)       │
│  Activities + Adapters + Layouts        │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      CAPA DE LÓGICA DE NEGOCIO          │
│  Repositories (CRUD Operations)         │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│      CAPA DE DATOS (Persistencia)       │
│  DatabaseHelper + SQLite                │
└─────────────────────────────────────────┘
```

### Componentes Principales

#### 🗄️ DatabaseHelper
- Crea y gestiona la base de datos SQLite
- Define esquema de tablas
- Maneja versiones de BD

#### 📊 Data
- `Cliente` - Información de clientes
- `Producto` - Catálogo de productos
- `Pedido` - Órdenes de compra
- `PedidoProducto` - Relación N:M
- `Factura` - Comprobantes de venta

#### 📚 Repositories
Cada repositorio implementa operaciones CRUD:

**ClienteRepository**
- `obtenerTodos()` - Obtener lista completa
- `obtenerPorId(id)` - Un cliente específico
- `insertar(cliente)` - Crear nuevo
- `actualizar(cliente)` - Editar existente
- `eliminar(id)` - Borrar
- `buscarPorNombre(nombre)` - Búsqueda

**ProductoRepository** - Similares más:
- `obtenerPorRangoValor(min, max)` - Filtro por precio

**PedidoRepository** - Plus:
- `obtenerPorCliente(id)` - Pedidos de un cliente
- `obtenerConDetalle(id)` - Con productos

**FacturaRepository** - Plus:
- `calcularIngresos()` - Total de ventas

#### 🎬 Activities
- `MainActivity` - Menú principal con navegación
- `ClientesActivity` - CRUD de clientes
- `ProductosActivity` - CRUD de productos
- `PedidosActivity` - CRUD de pedidos
- `FacturasActivity` - Facturas y reportes

#### 🔄 Adapters
- `ClienteAdapter` - Mostrar clientes en lista
- `ProductoAdapter` - Mostrar productos
- `PedidoAdapter` - Mostrar pedidos
- `FacturaAdapter` - Mostrar facturas

---

## 🎯 Flujos Principales

### Crear un Cliente
```
Activity UI
  ↓ Usuario toca "+ Agregar"
AlertDialog (entrada de datos)
  ↓ Usuario ingresa datos
Validación (campo no vacío)
  ↓ Verificación exitosa
ClienteRepository.insertar()
  ↓ Operación SQL
SQLite (persistencia)
  ↓ Éxito
RecyclerView actualiza
  ↓ Toast confirma
Usuario ve nuevo cliente
```

### Generar Factura
```
FacturasActivity
  ↓ Usuario ingresa ID Pedido
PedidoRepository.obtenerPorId()
  ↓ Busca pedido
PedidoRepository.obtenerConDetalle()
  ↓ Obtiene productos y cliente
Calcula valor total
  ↓ SUM(cantidad × precio)
FacturaRepository.insertar()
  ↓ Crea factura
calcularIngresos()
  ↓ Actualiza total en tiempo real
UI se refresca
  ↓ Nueva factura visible
```

---

## 🔐 Características de Seguridad

✅ **Integridad Referencial**
- Foreign Keys en todas las relaciones
- Cascada de eliminación

✅ **Validación de Datos**
- Campos obligatorios verificados
- Tipos de datos controlados

✅ **Normalización (3FN)**
- Sin redundancia de datos
- Relaciones bien definidas

✅ **Manejo de Errores**
- Try-catch en operaciones BD
- Toast para mensajes de error
- Logs para debugging

---

## 📈 Estadísticas del Proyecto

| Métrica | Cantidad |
|---------|----------|
| Archivos Kotlin | 24 |
| Archivos XML | 16 |
| Líneas de Código | 3,500+ |
| Métodos CRUD | 30+ |
| Tablas BD | 5 |
| Data Classes | 11 |
| Documentos | 6 |
| **Total de Archivos** | **46** |

---


## 📄 Licencia

Este proyecto está disponible bajo la licencia MIT.

```
MIT License

Copyright (c) 2026 Natram (Estiven Cano Rendón)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

Puedes usar, modificar y distribuir este código para propósitos educativos.

---

<div align="center">

**Hecho con ❤️ **

[⬆ Volver arriba](#ferretería-rodamientos-y-fierros---app-crud-android)

</div>
