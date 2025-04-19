# Spring Boot Astrapay Base Project
Berikut adalah Base Project untuk aplikasi Spring Boot, yang sesuai dengan konvensi yang digunakan pada Astrapay.

Lakukan run applikasi pada AstrapayBaseExternal

#### Author Information:  
- Arthur Purnama
- Anton Rifco Susilo

## Package Structure
```
com
 +- astrapay
     +- [ServiceName]Application.java
     +- [service name]
         +- batch
             +- listener
                +- [Name]ReadListener.java
                +- [Name]ProcessListener.java
                +- [Name]WriteListener.java
                +- [Name]JobListener.java
             +-[Name]Batch.java
         +- client
             +- dto
                +- [Name]Dto.java
                +- [Name]RequestDto.java
             +- [Name]Client.java
         |
         +- configuration
         |   +- mapper
         |      +- [Name]Mapper
         |   +- [Name]Configuration.java
         |
         +- controller
             +- advice
                 +- [Name]Advice.java
             +- [Name]Controller.java
         |
         +- dto
             +- [Name]Dto.java
             +- [Name]RequestDto.java
         |
         +- exception
             +- [Name]Exception.java
         |
         +- entity
             +- [Name].java
         |
         +- enums
             +- [Name].java
         |
         +- repository
             +- [Name]Repository.java
         |
         +- security
             +- [Name]Security.java
         |
         +- service
             +- model
                +- [Name]Model.java
             +- [Name]Service.java
         |
         +- validator
             +- constraint
                 +- [Name].java
             +- [Name]Validator.java
```


## Resources Structure
```
resources
 +- application.properties
 +- application-prd.properties
 +- application-sit.properties
 +- application-uat.properties
 +- sql
    +- data.sql
    +- schema.sql
 +- Resource Bundle 'messages'
    +- messages.properties
    +- messages_id.properties
    +- messages_de.properties
```

### Configuration files

Configuration files untuk project spring AstraPay harus menggunakan key-value properties file. Setiap environment dipisah sebagai berikut

-   application.properties : Konfigurasi untuk local environment product developer
    
-   application-sit.properties: Konfigurasi untuk System Integration Test environment
    
-   application-uat.properties: Konfigurasi untuk User Acceptance Test environment
    
-   application-prd.properties: Konfigurasi untuk Production environment
    

### SQL files

Untuk file schema dan data yang digunakan dalam database terutama ketika testing di local environment menggunakan H2 in memory atau file database. File sql dapat diatur di configuration file, akan tetapi standard spring boot adalah `data.sql` untuk initialisasi data dan `schema.sql` untuk initialisasi schema.

### Resource Bundle

Internationalization files untuk berbagai macam messages yang ditampilkan di REST payload. Mengikuti aturan standard Spring untuk Internationalization resource bundle yaitu `messages.properties` untuk bahasa standard (umumnya english) dan `messages_[country code].properties` untuk terjemahan seperti misalnya `messages_id.properties` untuk indonesia

## Package

Nama package harus dimulai dengan **com.astrapay** lalu disusul dengan nama servicenya. Jika servicenya adalah payment maka nama paketnya menjadi **com.astrapay.payment**.

## Application

File main Application harus diletakkan di posisi folder root di atas kelas lain. Karena anotasi @SpringBootApplication memicu scanning semua komponen didalam paket dan sub-paketnya. Nama class adalah nama service nya. Jadi jika service nya bernama payment maka nama file nya **PaymentApplication**.

```
@SpringBootApplication
public class PaymentApplication {
  /**
   * Main app entry point
   */
  public static void main(String[] args) {
    SpringApplication.run(PaymentApplication.class, args);
  }
}
```

## Client

Folder ini adalah untuk class yang berkomunikasi dengan system atau service lain menggunakan Spring`RestTemplate`. Penamaan class diakhiri dengan `Client` dalam namanya. Contoh `AccountClient`.

RestTemplate object harus didefinisikan terlebih dahulu di Configuration class lalu di inject di Client Class. Jika aplikasi memiliki lebih dari 1 (satu) client, maka setiap bean configuration harus diberikan nama bean yang akan diinject menggunakan anotasi @Qualifier.

Contoh:
```
@Configuration
public class ProductClientConfiguration {

    @Value("${product.username}")
    private String productServiceUsername;

    @Value("${product.password}")
    private String productServicePassword;

    @Bean(name = "restTemplateForProductClient")
    public RestTemplate prepareRestTemplateForProductService() {

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(productServiceUsername, productServicePassword));

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(1000);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());
        CloseableHttpClient httpClient = httpClientBuilder.build();

        HttpComponentsClientHttpRequestFactory rf = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(rf);
    }
}

@Component
public class ProductClient {
  @Autowired
  @Qualifier("restTemplateForProductClient")
  RestTemplate restTemplateForProductClient;
  
  restTemplateForProductClient.......
}
```

## Configuration

Folder ini adalah untuk semua spring bean java based configuration class. Setiap nama class harus diakhiri dengan `Configuration.java` dibelakangnya, jadi misalkan security bisa menggunakan `SecurityConfiguration.java`.

Setiap Configuration class harus memiliki tujuan dan maksud penciptaan java beans yang jelas mengikuti prinsip [single responsibility](https://www.baeldung.com/java-single-responsibility-principle "https://www.baeldung.com/java-single-responsibility-principle") dan [high cohesion](https://madooei.github.io/cs421_sp20_homepage/cohesion/ "https://madooei.github.io/cs421_sp20_homepage/cohesion/").

Setiap class harus memiliki anotasi `@Configuration` dimana setiap method untuk menciptakan spring bean harus memiliki anotasi `@Bean`.

Contoh:

```
@Configuration
public class ExampleConfig {
    // You can use all of the standard DI annotations inside this class.
    @Autowired
    private SomeClass someClass;

    // This is the definition of a Bean of type AnotherClass.
    // As shown, method arguments are another way to pass in dependencies.
    @Bean
    public AnotherClass anotherClass(SomeClass someClass) {
        return new AnotherClass(someClass);
    }

    // Other definitions...
}
```

## Controller

Folder ini adalah untuk Spring MVC Controller. Karena AstraPay menggunakan Java dan Spring spesifik untuk implementasi backend RESTful web services, maka controller yang digunakan hanya REST Controller.

Setiap nama class controller harus diakhiri dengan `Controller.java` dibelakangnya, jadi misalkan payment menjadi`PaymentController.java`.

Contoh sebuah REST Controller sebagai berikut

```
@RestController
@Api(value = "Account controller")
public class AccountController {
  @Autowired
  private AccountService accountService;

  @GetMapping("/accounts")
  @ApiOperation(value = "Get latest balance")
  @ApiResponses(
    value = {
      @ApiResponse(code = 200, message = "OK", response = AccountDto.class)
    }
  )
  public Page<AccountDto> getAllAccountsByUserId(
    @Validated(AccountDtoGroup.class) AccountDto accountDto,
    Pageable pageable
  ) {
    return accountService.getAllAccountsByUserId(accountDto.getUserId(), pageable);
  }

}
```

Aturan lebih lengkap tentang implementasi REST Controller dapat dilihat pada dokumen [Spring RESTful Web Services](https://astrapay.atlassian.net/wiki/spaces/PD/pages/1565098966)

### Advice

Folder ini bertujuan untuk menyimpan Spring Global Rest Exception dan Binder handling. Setiap class memiliki nama yang diakhiri dengan `Advice`. Setiap class harus dideklarasikan dengan anotasi `@RestControllerAdvice`. Setiap exception yang ingin ditangkap dan di handle harus di deklarasikan disini menggunakan `@ExceptionHandler` dan di deklarasikan juga response statusnya menggunakan `@ResponseStatus`.

Contoh:

```
@ControllerAdvice
class GlobalControllerExceptionHandler {
    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleConflict() {
        // Nothing to do
    }
}
```

## Data Transfer Object - Pada Controller

Data Transfer Object di AstraPay adalah Data Object yang digunakan ketika data atau informasi **meninggalkan** aplikasi/service/system atau **datang** dari aplikasi/service/system lain.

Data Transfer Object jenis ini digunakan hanya di Controller.

DTO ini merefleksikan endpoint parameter yang kita tuliskan di dokumentasi API. Dengan kata lain DTO pada Contoller adalah kontrak endpoint API kita dengan service lain yang ingin menggunakan endpoint kita.

Untuk aturannya, setiap nama dto harus diakhiri dengan `Dto.java` dibelakangnya, jadi misalkan payment bisa menggunakan `PaymentDto.java`.

Buat DTO untuk setiap request endpoint dan DTO lain secara terpisah untuk setiap response. Menggunakan DTO terpisah memungkinkan kita untuk mengoptimalkan transfer data. Hal ini juga dapat membantu untuk membatasi akses ke property yang tidak ingin diekspos. Dengan cara seperti ini, data yang bisa diekspose dapat didefinisikan dengan tepat dan dapat dikontrol dengan mudah.

Jika elemen atau property umum dan sama teridentifikasi pada beberapa endpoints, maka **cukup buat satu DTO yang digunakan di beberapa endpoints tersebut baik untuk request ataupun response nya**.

```
public class ProductRequestDto {
    private String name;
    private Double price;
}

public class ProductDto {
    private Long id;
    private String name;
    private Double price;
}
```

Aturan lebih lengkap tentang implementasi Data Transfer Object dapat dilihat pada dokumen [Spring RESTful Web Services](https://astrapay.atlassian.net/wiki/spaces/PD/pages/1565098966)

## Exception

Folder ini adalah untuk custom Exception. Aturan dan pertimbangan berikut harus diikuti jika ingin membuat custom exception.

### Harus memiliki manfaat dan tujuan yang jelas

Setiap custom exception harus memiliki manfaat dan tujuan yang jelas. Custom exception harus memberikan informasi atau fungsionalitas tambahan yang bukan bagian dari standard exception atau exception lain yang disediakan oleh framework.

Jika tidak ada manfaat dan tujuan nya, maka silahkan gunakan exception yang sudah disediakan oleh JDK atau framework yang sudah umum diketahui oleh semua product developer.

### Naming convention

Setiap nama exception harus diakhiri menggunakan `Exception` dibelakangnya. Contohnya `MyCustomException`.

Tujuan dari exception harus menentukan apakah custom exception ini harus berupa [checked atau unchecked exception](https://www.geeksforgeeks.org/checked-vs-unchecked-exceptions-in-java "https://www.geeksforgeeks.org/checked-vs-unchecked-exceptions-in-java").

Gunakan _checked exception_ untuk semua event luar biasa yang tidak dapat diantisipasi dan **harus ditangani dalam aplikasi**. _Checked exception_ harus menurunkan kelas Exception

Contoh :

```
public class MyCustomCheckedException extends Exception {
    public MyCustomException(String message) {
        super(message);
    }
}
```

Gunakan unchecked exception untuk semua internal error yang tidak dapat diantisipasi dan dimana aplikasi **tidak dapat atau tidak boleh recover**. Unchecked exception harus menurunkan kelas RuntimeException

Contoh:

```
public class MyCustomUncheckedException extends RuntimeException {
    public MyCustomException(String message) {
        super(message);
    }
}
```

### Berikan Javadoc comments

Javadoc harus menjelaskan tujuan dan maksud dari exception tersebut dan dalam situasi apa exception tersebut harus muncul. Tujuannya adalam membantu product developer lain untuk memahami code kalian dengan lebih baik dan menghindari kesalahan yang tidak perlu.

### Definisikan Constructor yang menentukan existensinya

Karena custom exception harus memiliki manfaat dan tujuan yang jelas, maka harus ada juga constructor yang dapat menyatakan atau menentukan existensi dari custom exception tersebut.

Contoh:

```
public class MyCustomCheckedException extends Exception {
    ...
    public MyCustomException(String message, Throwable cause, ErrorCode code) {
		super(message, cause);
		this.code = code;
	}
	...
}
```

## Entity

Pembuatan class model cukup menggunakan nama entitas yang dibutuhkan. Sebagai contoh kita ingin membuat model payment. Maka akan menjadi `Payments.class` dengan contoh sebagai berikut :

```
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @ManyToOne(targetEntity = Transaction.class)
    @JoinColumn(name = "transaction_id", insertable = false, updatable = false)
    private Transaction transactionId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    protected void prePersist() {
        this.brandId = String.valueOf(UUID.randomUUID());
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

Pada model diharapkan penggunaan annotation di perluas, seperti penggunakan column name, penggunaan lombok project.

## Enums

Enum bukan Constants. Penggunaan enum di Java memberikan banyak kelebihan dalam membuat implementasi yang lebih baik. Jika sebuah constant memiliki relasi logik terhadap sebuah class, maka harus di definisikan dalam class tersebut. Hanya jika satu set constant teridentifikasi sebagai sebuah enumerasi, maka harus didefinisikan sebagai enum.

Penggunaan enum men support Type Safe dan memaksa pembatasan terhadap value yang bisa digunakan. Penamaan enum tidak menggunakan prefix dan suffix.

Contoh:
```
`//OK String playerType =  Constants.MALE; // Compile-time error - incompatible types! PlayerType playerType =  Gender.MALE;`
```
Enum juga mendukung polymorphic design principle.

Contoh:

```
@Test
public void paper_beats_rock() {
	assertThat(PAPER.beats(ROCK)).isTrue();
	assertThat(ROCK.beats(PAPER)).isFalse();
}
@Test
public void scissors_beats_paper() {
	assertThat(SCISSORS.beats(PAPER)).isTrue();
	assertThat(PAPER.beats(SCISSORS)).isFalse();
}
@Test
public void rock_beats_scissors() {
	assertThat(ROCK.beats(SCISSORS)).isTrue();
	assertThat(SCISSORS.beats(ROCK)).isFalse();
}

public enum Gesture {
	ROCK() {
		// Enums are polymorphic, that's really handy!
		@Override
		public boolean beats(Gesture other) {
			return other == SCISSORS;
		}
	},
	PAPER, SCISSORS;

	// we can implement with the integer representation
	public boolean beats(Gesture other) {
		return ordinal() - other.ordinal() == 1;
	}
}
```

## Repository

Setiap nama repository harus menggunakan `Repository.java` dibelakangnya, misalkan kita ingin membuat repository untuk payment, maka buatlah menjadi `PaymentRepositoy.java`. Pada repository diharapkan tidak menggunakan native query.

Berikut contohnya :
```
@Query(value = "select customer from AstrapayCustomer customer") 
LinkedList<AstrapayCustomer>  getCustomer();
```

## Security

Setiap nama security harus diakhir dengan `Security.java` dibelakangnya, misalkan kita ingin membuat security untuk Api, maka menjadi `ApiSecurity.java`.

Security berfungsi untuk menangani seluruh keamanan pada Rest Api sebelum data masuk pada Controller. Beberapa fungsionalitas yang akan masuk pada security antara lain [CSRF](https://owasp.org/www-community/attacks/csrf#:~:text=Cross%2DSite%20Request%20Forgery%20(CSRF,which%20they're%20currently%20authenticated. "https://owasp.org/www-community/attacks/csrf#:~:text=Cross%2DSite%20Request%20Forgery%20(CSRF,which%20they're%20currently%20authenticated.") (Cross Site Request Forgery), encryption, decryption.

Contoh:
```
@Configuration
@EnableWebSecurity
public class ApiSecurity extends WebSecurityConfigurerAdapter {
  
   @Override
   protected void configure(HttpSecurity http) throws Exception {
    http
      .csrf()
      .disable()
      .authorizeRequests()
      // Enabled swagger end points
      .antMatchers(
        "/v2/api-docs",
        "/configuration/**",
        "/swagger*/**",
        "/webjars/**"
      )
      .permitAll()
      ;
  }
}
```

## Service

Setiap nama service harus diakhiri dengan `Service.java` dibelakangnya, misalkan kita ingin membuat service untuk payment, maka menjadi `PaymentService.java`.

Ketika sebuah service hanya memiliki **1(Satu)** Implementasi konkrit maka **tidak perlu membuat interface**. Membuat interface hanya untuk satu implementasi konkrit seperti `PaymentServiceImpl` adalah sebuah anti-pattern.

Berikut alasannya:

1.  Jika kalian tiba tiba harus membuat implementasi lain nya (itu point nya membuat interface), mau dinamain apa implementasinya? `PaymentServiceImpl2` ?
    
2.  Setiap kali kita membuat service, kita punya 2 artefak yang hanya meningkatkan kompleksitas implementasi kita.
    
3.  Membuat dokumentasi Javadocs menjadi redundant
    
4.  Navigasi menggunakan IDE menjadi tidak mudah.
    
5.  Menamakan class dengan Suffix Impl seperti menyatakan “saya tidak tahu lagi harus namain apa”, dan ini adalah tanda-tanda code smell
    
6.  Jika alasannya adalah mocking object ketika unit testing, mockito juga sudah lama bisa [mocking concrete object](https://javadoc.io/static/org.mockito/mockito-core/3.10.0/org/mockito/Mockito.html#stubbing "https://javadoc.io/static/org.mockito/mockito-core/3.10.0/org/mockito/Mockito.html#stubbing")
    

## Validator

Folder ini bertujuan untuk menyimpan semua bean validation sesuai spesifikasi [JSR-380 Bean Validation 2.0](https://beanvalidation.org/2.0/spec/ "https://beanvalidation.org/2.0/spec/"). Setiap class memiliki nama yang diakhiri dengan `Validator`. Setiap class meng implements `ConstraintValidator`.

Contoh:
```
public class CheckSumValidator implements ConstraintValidator<CheckSum, QrisPayload> {

    @Override
    public boolean isValid(QrisPayload value, ConstraintValidatorContext context) {
        String crcCheckSum = generateChecksum(value.getPayload().substring(0, value.getPayload().length()-4));
        return crcCheckSum.equals(value.getQrisRoot().get(63).getValue());
    }

    private String generateChecksum(String payload) {
        int checksum = 0xffff;
        int polynomial = 0x1021;
        byte[] data = payload.getBytes(StandardCharsets.UTF_8);
        for (byte b : data) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((checksum >> 15 & 1) == 1);
                checksum <<= 1;
                if (c15 ^ bit) {
                    checksum ^= polynomial;
                }
            }
        }
        checksum &= 0xffff;
        return String.format("%04X", checksum);
    }
}
```

### Constraint

Folder ini adalah untuk mengimplementasi custom Annotation untuk bean validation. Penamaan class tidak memiliki prefix dan suffix.

Contoh:

```
@Documented
@Constraint(validatedBy = {CheckSumValidator.class})
@Target({TYPE})
@Retention(RUNTIME)
public @interface CheckSum {

    /**
     *
     * @return String
     */
    String message() default "Checksum wajib dihitung sesuai dengan [ISO/IEC 13239]";

    /**
     *
     * @return class
     */
    Class<?>[] groups() default {};

    /**
     *
     * @return class
     */
    Class<? extends Payload>[] payload() default {};
}
```
