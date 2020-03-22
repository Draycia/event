/*
 * This file is part of event, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostResultEventBusTest {
  private final EventBus<Integer> bus = new SimpleEventBus<>(Integer.class);

  @Test
  void testPostResult() {
    this.bus.register(Integer.class, event -> {
      if(event % 5 == 0) {
        throw new Throwable();
      }
    });
    this.bus.register(Integer.class, event -> {
      if(event % 2 == 0) {
        throw new Exception();
      }
    });

    final PostResult result1 = this.bus.post(7);
    assertTrue(result1.wasSuccessful());
    assertDoesNotThrow(result1::raise);

    final PostResult result2 = this.bus.post(5);
    assertFalse(result2.wasSuccessful());
    assertThrows(PostResult.CompositeException.class, result2::raise);
    assertEquals(1, result2.exceptions().size());
    assertEquals(Throwable.class, result2.exceptions().values().iterator().next().getClass());

    final PostResult result3 = this.bus.post(10);
    assertFalse(result3.wasSuccessful());
    assertEquals(2, result3.exceptions().size());
  }
}
