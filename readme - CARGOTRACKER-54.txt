WildFly config changes:
* Added eclipselink-2.5.1.jar to modules\system\layers\base\org\eclipse\persistence\main and modified module.xml:
        <resource-root path="eclipselink-2.5.1.jar"/>
		<module name="javax.ws.rs.api"/>

Code changes:
* Adding maven profiles for glassfish (default) and wildfly
* Added wildfly web.xml with resteasy servlet
* Added default constructors for REST service classes to support resteasy
* Added some logging


TODO
* standardize JDBC JNDI name
* put datasource in web.xml
* maven web.xml filtering and token replacement to add resteasy servlet???
