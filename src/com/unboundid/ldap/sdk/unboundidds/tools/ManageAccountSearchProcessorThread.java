/*
 * Copyright 2016 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2016 UnboundID Corp.
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
package com.unboundid.ldap.sdk.unboundidds.tools;



/**
 * This class provides a thread that may be used to parallelize the process of
 * searching for entries on which to invoke password policy state operations.
 * <BR>
 * <BLOCKQUOTE>
 *   <B>NOTE:</B>  This class is part of the Commercial Edition of the UnboundID
 *   LDAP SDK for Java.  It is not available for use in applications that
 *   include only the Standard Edition of the LDAP SDK, and is not supported for
 *   use in conjunction with non-UnboundID products.
 * </BLOCKQUOTE>
 */
final class ManageAccountSearchProcessorThread
       extends Thread
{
  // The manage-account search processor that will actually do the majority of
  // the work.
  private final ManageAccountSearchProcessor searchProcessor;

  // The search operation currently being processed by this thread.
  private volatile ManageAccountSearchOperation activeSearchOperation;



  /**
   * Creates a new manage-account search processor thread with the provided
   * information.
   *
   * @param  threadNumber     The thread number for this thread.  This will only
   *                          be used for informational purposes in the thread
   *                          name.
   * @param  searchProcessor  The manage-account search processor that will
   *                          actually do the majority of the work.  It must not
   *                          be {@code null}.
   */
  ManageAccountSearchProcessorThread(final int threadNumber,
       final ManageAccountSearchProcessor searchProcessor)
  {
    setName("manage-account Search Processor Thread " + threadNumber);

    this.searchProcessor = searchProcessor;

    activeSearchOperation = null;
  }



  /**
   * Performs the processing for this thread.
   */
  @Override()
  public void run()
  {
    while (true)
    {
      try
      {
        activeSearchOperation = searchProcessor.getSearchOperation();
        if (activeSearchOperation == null)
        {
          return;
        }
        else
        {
          activeSearchOperation.doSearch();
        }
      }
      finally
      {
        activeSearchOperation = null;
      }
    }
  }



  /**
   * Cancels processing on the active search operation.
   */
  void cancelSearch()
  {
    final ManageAccountSearchOperation o = activeSearchOperation;
    if (o != null)
    {
      o.cancelSearch();
    }
  }
}
