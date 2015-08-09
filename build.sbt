name := "dbflute_lift"

version := "1.0.0-SNAPSHOT"

resolvers ++= Seq("snapshots"     at "https://oss.sonatype.org/content/repositories/snapshots",
                  "staging"       at "https://oss.sonatype.org/content/repositories/staging",
                  "releases"      at "https://oss.sonatype.org/content/repositories/releases"
                 )

resolvers += "The Seasar Foundation Maven2 Repository" at "http://maven.seasar.org/maven2"

webSettings

scalaVersion := "2.11.6"

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

scalacOptions ++= Seq("-deprecation", "-unchecked")

libraryDependencies ++= {
  val liftVersion = "2.6.2"
	Seq(
		"net.liftweb"       %% "lift-webkit"        % liftVersion        % "compile",
		"net.liftmodules"	%% "jta_2.6"			% "1.2-SNAPSHOT"	 % "compile",
	    "net.liftweb"       %% "lift-mapper"        % liftVersion        % "compile",
	    "net.liftmodules"   %% "lift-jquery-module_2.6" % "2.8",
	    "org.eclipse.jetty" % "jetty-webapp"        % "8.1.7.v20120910"  % "container,test",
	    "org.eclipse.jetty" % "jetty-plus"          % "8.1.7.v20120910"  % "container,test", // For Jetty Config
	    "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
	    "ch.qos.logback"    % "logback-classic"     % "1.0.6",
	    "org.specs2"        %% "specs2"             % "2.3.12"             % "test",
	   //"com.h2database"    % "h2"                  % "1.3.167" 
	  "org.seasar.dbflute" % "dbflute-runtime" % "1.0.5M",
	  "com.h2database" % "h2" % "1.4.178" % "runtime",
	  "com.github.nscala-time" %% "nscala-time" % "2.0.0",
	  "com.google.inject" % "guice" % "3.0",
	  "org.apache.geronimo.specs" % "geronimo-jta_1.1_spec" % "1.0" % "runtime",
	  "log4j" % "log4j" % "1.2.14" % "runtime",
	  "org.seasar.dbflute" % "utflute-guice" % "0.4.6" % "test",
	  "junit" % "junit" % "4.8.2" % "test",
	  "org.scalatest" %% "scalatest" % "3.0.0-SNAP4" % "test"
	)
}

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.withSource := true
