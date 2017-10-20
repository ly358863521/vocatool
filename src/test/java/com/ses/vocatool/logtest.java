package com.ses.vocatool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class logtest {
    public static Logger logger = LoggerFactory.getLogger(logtest.class);

    public static void main(String[] args) {
        logger.debug("here");
    }
}
