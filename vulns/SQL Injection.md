## SQL Injection

#### Description

SQL Injection vulnerabilities are a class of vulnerability where attackers can cause malicious code to be exected on the server. Often times, untrusted data that is unproperly validated can end up in query strings sent to a database, and become indistinguishable from queries the developer is making. Since the malicious data will appear in-stream with the intended code, it will also execute with the same privileges as the original operation, creating a security risk. SQL Injection attacks can be used to bypass authentication or authorization, change stored information about user accounts, delete existing information in the database, and more.

The most common avenue for SQL injection attacks is when unverified input is directly concatenated to the query string. For example, let's assume we use the following query to check if a user exists and is has an ```access_level``` of 1.

```
SELECT 1 FROM USER WHERE user_id = " + user_id + " AND access_level = 1
```

This will allow us to properly verify user ```access_level```. However, if the user_id is not properly sanitized, the attacker can inject SQL code (where data should be) and bypass an ID check. If the attacker could pass ```1 OR 1=1``` as the ```user_id```, they would transform the query into the following:

```
SELECT 1 FROM USER WHERE user_id = 1 OR 1=1 AND access_level = 1
```

This transforms the query entirely. While it originally checked if the given user had an ```access_level``` of 1, it now simply checks if 1 = 1, which is obviously always true. Though this example is already bad enough, SQL injections can be far more malicious than simply bypassing a permissions check. Consider the following, where the ```user_id``` is passed as ```1; DROP TABLE user; --```. Our simple query then becomes:

```
SELECT 1 FROM USER WHERE user_id = 1; DROP TABLE user; -- AND access_level = 1
```

The attacker has now successfully dropped our entire users table!

One important point is that simply hiding the result of a query is not enough to prevent SQL injection attacks from occuring. One class of attack, known as [Blind SQL Injections](https://www.owasp.org/index.php/Blind_SQL_Injection), operate on the premise that an application is vulnerable to attack but does not have direct output based on the result of the query.

#### Code Snippet

src/main/java/com/nVisium/androidnv/api/controller/PaymentController.java

```
@Transactional
@RequestMapping(value = "/balance", method = {RequestMethod.GET, RequestMethod.POST})
public String balance(
		@RequestParam(value = "amount", required = false) String amount,
		@RequestParam(value = "creditcard", required = false) String creditcard,
		RedirectAttributes redirectAttrs,
		Model model) {
	
	if (creditcard == null || amount == null) {
		model.addAttribute("user", security.getSecurityContext().getUser());
		return "payment/balance";
	}

	em.joinTransaction();
	em.createNativeQuery("Update Users u set balance = balance + " + amount + "where id = " + security.getCurrentUserId()).executeUpdate();
	
	// ---
```
src/main/java/com/nVisium/androidnv/api/controller/PaymentController.java

```
@RequestMapping(value = "/list-sent/{id}", method = RequestMethod.GET)
public String listSentPayments(
		@PathVariable String id,
		Model model) {
	
	@SuppressWarnings("unchecked")
	List<Payment> payments = 
			em.createNativeQuery("select * from Payments p where p.sender = " + id, Payment.class)
			.getResultList();
		
	// ---
```
src/main/java/com/nVisium/androidnv/api/controller/PaymentController.java

```
@RequestMapping(value = "/list-received/{id}", method = RequestMethod.GET)
public String listReceivedPayments(
		@PathVariable String id,
		Model model) {
	
	//List<Payment> payments = paymentService.getReceivedPayments(userService.loadUserById(id));
	@SuppressWarnings("unchecked")
	List<Payment> payments = 
			em.createNativeQuery("select * from Payments p where p.receiver = " + id, Payment.class)
			.getResultList();
			
	// ---
```

#### Problem

MoneyX's has three difference instances of raw SQL queries in its ```PaymentController```. On both the pages to list sent payments and to list received payments, the sysem executes a raw ```SELECT * FROM PAYMENTS``` query to pull any relevant rows from the table. In the user balance update method, it performs the addition to the user balance with another raw SQL query.

In all three cases, the code does not adequately protect against SQL injection attacks. The query is made by simply concatenating user input before it is send to the database, and there are no assurances that the user really inputs an ID or dollar amount like they are supposed to.

#### Solution

Luckily for developers, there are a number of easy and effective ways to defend against SQL attacks. One very straightforward way is to perform input validation on the user input. This is usually in the form of a regular expression that ensures the user has entered a valid string. For the case of the "list-sent" payments page, we can ensure the string passed contains a valid ID by using the following regular expression.

```
// 'id' variable is the user parameter
String pattern = "[\d]+";

Matcher idMatch = Pattern.compile(pattern).matcher(id);
if (m.find()) {
	/* User ID is in m.group(0) */
} else {
	/* Show error message */
}

```

However, using input validation is finicky. Problems with regular expressions may lead to inadvertant holes in the application. They are often overkill for simple uses (like pulling a user ID integer from an unvalidated input) but are often too complex and brittle for larger inputs.

Another method of preventing SQL injections is to escape characters in the string that may prove problematic. This approach applied to the "list-received" payments page would remove any non-digit characters from the string, or characters that are known parts of a SQL command. For character escaping, it is best to use an established library, like [ESAPI](https://www.owasp.org/index.php/ESAPI), rather than attempting to implement your own solution. However, this method is again hard to get correct and can still cause problems down the line with overlooked characters.

By far the most effective methods to prevent SQL injection are through the use of *parameterized queries* and *prepared statements*. Parameterized queries separate out the logic portions of the query request from the data portions. The query is initially constructed using placeholders for where user input will be inserted. When it is called, the input is treated as data and any SQL it contains will not be executed. We can find implementations of these for the "list-sent" and "list-received" lookups in ```PaymentRepository```.

```
@Query("select p from Payment p where p.sender = ?1")
public List<Payment> findPaymentsBySender(User sender);

@Query("select p from Payment p where p.receiver = ?1")
public List<Payment> findPaymentsByReceiver(User receiver);
```

With Hibernate, calling these functions will seperate out the information needed for accessing the data from the receiver or sender information itself. Even if the value supplied by the user contains malicious SQL, it will not be executed because it is treated like data! Using parameterized queries can also help make your code more organized, because it separates out the query from the data passed to the query.

Prepared/Stored Statements are similar to parameterized queries, but the query is sent to the database before it is executed, containing placeholders where input will go. The database then executes it with the data it receives. Prepared Statements also prevent SQL injection, but move the solution to the database itself rather than the application.