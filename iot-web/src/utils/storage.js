export function getItem(name) {
  const data = window.localStorage.getItem(name)
  try {
    return JSON.parse(data)
  }
  catch (err) {
    return data
  }
}

export function setItem(name, value) {
  if (typeof value === 'object') {
    value = JSON.stringify(value)
  }

  window.localStorage.setItem(name, value)
}

export function removeItem(name) {
  window.localStorage.removeItem(name)
}
