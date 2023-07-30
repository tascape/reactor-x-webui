# reactor-x-webui
Reactor extension for web UI automation, based on Selenium WebDriver.

```
<dependency>
	<groupId>com.tascape</groupId>
	<artifactId>reactor-x-webui</artifactId>
	<version>1.5.0</version>
</dependency>
```
### local test report
  * [reactor-report README](https://github.com/tascape/reactor-report/blob/master/README.md)

### web browser support

#### Google Chrome
  * Install the Google Chrome for test, see https://googlechromelabs.github.io/chrome-for-testing/.
  * Provide Chrome binary path as a Java system property like: "-Dreactor.comm.webbrowser.BINARY=/Users/tascape/Google Chrome.app/Contents/MacOS/Google Chrome", when running the tests.
  * Download the corresponding Chrome Driver, unzip it as ~/.reactor/webui/chromedriver.

#### Mozilla Firefox
  * Install the latest Mozilla Firefox.
  * Download the latest [Firefox Geckodriver](https://github.com/mozilla/geckodriver/releases), unzip it as ~/.reactor/webui/geckodriver.
