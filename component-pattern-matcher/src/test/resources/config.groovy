log4j {
    appender.stdout = "org.apache.log4j.ConsoleAppender"
    appender."stdout.layout"="org.apache.log4j.PatternLayout"
    appender.scrlog = "org.apache.log4j.FileAppender"
    appender."scrlog.layout"="org.apache.log4j.PatternLayout"
    appender."scrlog.layout.ConversionPattern"="%d %5p %c{1}:%L - %m%n"
    appender."scrlog.file"="rootscript.log"
    rootLogger = "debug,scrlog,stdout"
}