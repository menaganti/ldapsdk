/*
 * Copyright 2008-2010 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2010 UnboundID Corp.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.util;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import static com.unboundid.util.UtilityMessages.*;



/**
 * This class defines a file value pattern component, which may be used provide
 * string values read from a specified local file.
 */
final class FileValuePatternComponent
      extends ValuePatternComponent
{
  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 2773328295435703361L;



  // The lines that make up the data file.
  private final String[] lines;

  // The random number generator that will be used to seed the thread-local
  // generators.
  private final Random seedRandom;

  // The random number generator that will be used by this component.
  private final ThreadLocal<Random> random;



  /**
   * Creates a new file value pattern component with the provided information.
   *
   * @param  path  The path to the file from which to read the data.
   * @param  seed  The value that will be used to seed the initial random number
   *               generator.
   *
   * @throws  IOException  If a problem occurs while reading data from the
   *                       specified file.
   */
  FileValuePatternComponent(final String path, final long seed)
       throws IOException
  {
    // Create the random number generators that will be used.
    seedRandom = new Random(seed);
    random     = new ThreadLocal<Random>();


    final LinkedList<String> lineList = new LinkedList<String>();
    final BufferedReader reader = new BufferedReader(new FileReader(path));

    try
    {
      while (true)
      {
        final String line = reader.readLine();
        if (line == null)
        {
          break;
        }

        lineList.add(line);
      }
    }
    finally
    {
      reader.close();
    }

    if (lineList.isEmpty())
    {
      throw new IOException(ERR_VALUE_PATTERN_COMPONENT_EMPTY_FILE.get());
    }

    lines = new String[lineList.size()];
    lineList.toArray(lines);
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  void append(final StringBuilder buffer)
  {
    Random r = random.get();
    if (r == null)
    {
      r = new Random(seedRandom.nextLong());
      random.set(r);
    }

    buffer.append(lines[r.nextInt(lines.length)]);
  }
}