package org.example.dzgrep.reader;

import org.example.dzgrep.entity.LogRecord;

import java.util.Iterator;

public interface LogReader extends Iterator<LogRecord>, AutoCloseable {}
