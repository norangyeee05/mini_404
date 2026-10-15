const databaseName = 'frontend-notice-attachments'
function openDatabase() {
  return new Promise((resolve, reject) => {
    const request = indexedDB.open(databaseName, 1)
    request.onupgradeneeded = () => request.result.createObjectStore('files')
    request.onsuccess = () => resolve(request.result)
    request.onerror = () => reject(request.error)
  })
}
export async function saveNoticeAttachment(id, file) {
  const db = await openDatabase()
  return new Promise((resolve, reject) => {
    const tx = db.transaction('files', 'readwrite')
    if (file) tx.objectStore('files').put(file, String(id))
    else tx.objectStore('files').delete(String(id))
    tx.oncomplete = () => { db.close(); resolve() }
    tx.onerror = () => { db.close(); reject(tx.error) }
    tx.onabort = () => { db.close(); reject(tx.error) }
  })
}
export async function getNoticeAttachment(id) {
  const db = await openDatabase()
  return new Promise((resolve, reject) => {
    const tx = db.transaction('files', 'readonly')
    const request = tx.objectStore('files').get(String(id))
    request.onsuccess = () => resolve(request.result)
    request.onerror = () => reject(request.error)
    tx.oncomplete = () => db.close()
  })
}
