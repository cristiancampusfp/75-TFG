🏋️‍♂️ Fitness PRO - Gestión Integral de Fitness
📝 Descripción del Proyecto
Fitness PRO es una plataforma web desarrollada como Trabajo de Fin de Grado (TFG) para la gestión, control y seguimiento de usuarios en un entorno deportivo. El sistema ofrece una arquitectura centralizada que permite al administrador gestionar el ciclo de vida completo de los atletas, mientras que el cliente dispone de un entorno personalizado para consultar su progresión, dieta y rutinas.

🚀 Funcionalidades Clave
🛡️ Panel de Administración (Control Total)
Gestión de Usuarios (CRUD): Creación, edición detallada y eliminación de usuarios.

Seguridad basada en Roles: Implementación de guardias de seguridad para restringir el acceso a rutas administrativas mediante Spring Security.

Integridad Referencial: Sistema de borrado en cascada (CascadeType.ALL) que garantiza que, al eliminar un usuario, el sistema limpie automáticamente todas sus dependencias (dietas, rutinas e historial de pesos), evitando registros huérfanos.

Control de Credenciales: Funcionalidad para forzar el reseteo de contraseñas de forma segura mediante encriptación BCrypt.

🏋️‍♂️ Panel de Usuario (Cliente)
Perfil Personal: Gestión de datos personales, objetivos y nivel de experiencia.

Seguimiento Nutricional: Interfaz enfocada en la visualización de planes nutricionales.

Gestión de Progreso: Acceso centralizado a rutinas de entrenamiento.

🛠️ Stack Tecnológico
Backend: Java 17+, Spring Boot, Spring Security (JWT), Spring Data JPA (Hibernate).

Frontend: HTML5, CSS3, JavaScript (Fetch API), Bootstrap 5.

Base de Datos: MySQL.

Entorno de desarrollo: IntelliJ IDEA.

Gestor de dependencias: Apache Maven.

⚙️ Arquitectura de Seguridad
La aplicación garantiza la integridad de los datos mediante:

Autenticación: Sistema de tokens JWT (JSON Web Tokens).

Autorización: Validación de roles en el cliente (LocalStorage) y protección a nivel de API.

Serialización Segura: Uso estratégico de @JsonIgnore para prevenir errores de recursividad (bucle infinito) en las relaciones bidireccionales entre entidades.

🚀 Puesta en Marcha
Clonar el repositorio:

Bash
git clone https://github.com/cristiancampusfp/75-TFG.git
Configuración del entorno:
Este proyecto utiliza variables de entorno para la configuración de la base de datos por motivos de seguridad. Asegúrate de configurar las siguientes variables en tu sistema o en el perfil de ejecución de IntelliJ:

DB_URL

DB_USER

DB_PASSWORD

Compilación y ejecución:
Utiliza Maven para ejecutar el proyecto desde IntelliJ:

Bash
mvn clean install
mvn spring-boot:run
👨‍💻 Autor
Proyecto desarrollado como Trabajo de Fin de Grado (TFG) bajo estándares de arquitectura limpia y seguridad.

GitHub: cristiancampusfp

