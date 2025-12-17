# VovkORM 🐺

**VovkORM** is a lightweight Object-Relational Mapping (ORM) framework for Java. It is designed to provide a simple, efficient, and transparent way to manage database interactions without the overhead of heavy, complex frameworks.

## 🚀 Installation

Currently, VovkORM can be installed manually by publishing it to your local Maven repository.

### 1. Build and Publish Locally
Open your terminal and run the following commands:

```bash
# Clone the repository
git clone https://github.com/nazariusPr/VovkORM.git
cd VovkORM

# Clean and build the project
./gradlew clean build

# Publish to your local Maven repository
./gradlew publishToMavenLocal
```

### 2. Add Dependency to Your Project

Once published locally, you can add VovkORM to your project's build configuration.

#### For Maven (`pom.xml`):

```xml
<dependency>
    <groupId>org.nazarius</groupId>
    <artifactId>vovkorm</artifactId>
    <version>1.0.0-BETA</version>
</dependency>
```

#### For Gradle (`build.gradle`):

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'org.nazarius:vovkorm:1.0.0-BETA'
}
```

### 3. How to use:

- Define your entity class with appropriate annotations.  
- Instantiate the `EntityManager` class by providing a `DataSource` (this can be a HikariCP connection pool or a simple DataSource implementation from VovkORM).


```java
@Table(name = "users")
public class UserEntity {
    @PrimaryKey(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "username", unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    private Integer age;

    // Public no-arg constructor for reflection
    public UserEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                '}';
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        EntityManager em = getEntityManager();

        System.out.println("\n----------------All Users----------------\n");
        List<UserEntity> users = em.readAll(UserEntity.class);
        for (UserEntity user : users) {
            System.out.println(user);
        }

        System.out.println("\n----------------Users older than 25----------------\n");
        Select select = select().from("users").where(column("age").gt(25));
        List<UserEntity> users2 = em.read(select, UserEntity.class);
        for (UserEntity user : users2) {
            System.out.println(user);
        }

    }

    private static EntityManager getEntityManager() {
        final String URL = "jdbc:postgresql://localhost:5432/postgres";
        final String USERNAME = "postgres";
        final String PASSWORD = "root";

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(10);
        config.setPoolName("VovkORMPool");

        DataSource dataSource = new HikariDataSource(config);
        return new SimpleEntityManager(dataSource);
    }
}
```

## 📝 To Do

- Add support for relationships (one-to-one, one-to-many, many-to-many)  
- Implement create operation that works independently of the database dialect  
- Expand broad support for multiple database dialects

## 👤 Author

**Nazar Prots** — passionate Java developer and creator of VovkORM.  
Feel free to reach out or contribute on [GitHub](https://github.com/nazariusPr).



