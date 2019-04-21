/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * RPTools Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implements a Map with case-insensitive keys.
 *
 * <p>This class needs to have a <a href="http://xstream.codehaus.org/converter-tutorial.html">
 * converter class</a> for use by XStream so that it can read/write them in such a manner that older
 * tokens (or other uses) will be upwardly compatible. This is currently a problem for tokens older
 * than 1.3.b54 that are being read by later versions.
 *
 * @author frank
 * @param <V> is the type of the value for the Map implementation
 */
public class CaseInsensitiveHashMap<V> implements Map<String, V> {
  private final Map<String, KeyValue> store = new HashMap<String, KeyValue>();

  public void clear() {
    store.clear();
  }

  protected Map<String, KeyValue> getStore() {
    return store;
  }

  private String normalizeKey(Object key) {
    if (key == null) return null;
    return ((String) key).toLowerCase();
  }

  public boolean containsKey(Object key) {
    return store.containsKey(normalizeKey(key));
  }

  public boolean containsValue(Object value) {
    for (Entry<String, KeyValue> entry : store.entrySet()) {
      if ((entry.getValue()).value == value) return true;
    }
    return false;
  }

  public Set<java.util.Map.Entry<String, V>> entrySet() {
    HashSet<Entry<String, V>> ret = new HashSet<Entry<String, V>>();

    for (Entry<String, KeyValue> entry : store.entrySet()) {
      ret.add(entry.getValue());
    }
    return ret;
  }

  public V get(Object key) {
    KeyValue kv = store.get(normalizeKey(key));
    if (kv == null) return null;
    return kv.getValue();
  }

  public boolean isEmpty() {
    return store.isEmpty();
  }

  /**
   * Returns a key Set<> which contains case-insensitive Strings.
   *
   * @return list of keys converted to lowercase
   */
  public Set<String> keySet() {
    HashSet<String> ret = new HashSet<String>();

    for (Entry<String, KeyValue> entry : store.entrySet()) {
      ret.add(entry.getKey());
    }
    return ret;
  }

  /**
   * Returns a key Set<> which contains case-sensitive Strings. This is done by reading all values
   * and retrieving their keys, since the keys in this HashMap have already been converted and can't
   * be used.
   *
   * @return list of keys in their original case
   */
  public Set<String> keySetRaw() {
    HashSet<String> ret = new HashSet<String>();

    for (Entry<String, KeyValue> entry : store.entrySet()) {
      ret.add(entry.getValue().getKey());
    }
    return ret;
  }

  public V put(String key, V value) {
    KeyValue oldValue = store.put(normalizeKey(key), new KeyValue(key, value));

    if (oldValue != null) return oldValue.getValue();
    return null;
  }

  public void putAll(Map<? extends String, ? extends V> m) {
    if (m.isEmpty()) return;

    for (Iterator<? extends Map.Entry<? extends String, ? extends V>> i = m.entrySet().iterator();
        i.hasNext(); ) {
      Map.Entry<? extends String, ? extends V> e = i.next();
      put(e.getKey(), e.getValue());
    }
  }

  public V remove(Object key) {
    KeyValue oldValue = store.remove(normalizeKey(key));

    if (oldValue != null) return oldValue.getValue();
    return null;
  }

  public int size() {
    return store.size();
  }

  public Collection<V> values() {
    List<V> ret = new ArrayList<V>();

    for (Entry<String, KeyValue> entry : store.entrySet()) {
      ret.add(entry.getValue().getValue());
    }
    return ret;
  }

  protected class KeyValue implements Map.Entry<String, V> {
    public String key;
    public V value;

    public KeyValue(String key, V value) {
      this.key = key;
      this.value = value;
    }

    public String getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }

    public V setValue(V value) {
      V oldValue = this.value;
      this.value = value;
      return oldValue;
    }

    @Override
    public final boolean equals(Object o) {
      if (o instanceof Map.Entry) {
        @SuppressWarnings("unchecked")
        Map.Entry<String, V> e = (Map.Entry<String, V>) o;
        Object k1 = getKey();
        Object k2 = e.getKey();
        if (k1 == k2 || (k1 != null && k1.equals(k2))) {
          Object v1 = getValue();
          Object v2 = e.getValue();
          if (v1 == v2 || (v1 != null && v1.equals(v2))) return true;
        }
      }
      return false;
    }

    @Override
    public final int hashCode() {
      return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }
  }
}
