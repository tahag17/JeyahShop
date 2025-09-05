export function convertDateArrayToDate(array?: number[] | null): Date | null {
  if (!array) return null;
  return new Date(
    array[0], // year
    (array[1] ?? 1) - 1, // month (default to Jan if missing)
    array[2] ?? 1, // day (default to 1)
    array[3] ?? 0, // hour
    array[4] ?? 0, // minute
    array[5] ?? 0, // second
    Math.floor((array[6] ?? 0) / 1_000_000) // nanos → ms
  );
}

export function toDate(value: any): Date | null {
  if (!value) return null;
  if (Array.isArray(value)) return convertDateArrayToDate(value);
  if (typeof value === 'string' || typeof value === 'number')
    return new Date(value);
  if (value instanceof Date) return value;
  return null;
}

export function convertToDate(input: any): Date | null {
  if (!input) return null;

  // If it's already a Date, just return it
  if (input instanceof Date) return input;

  // If it's an array from backend
  if (Array.isArray(input)) return convertDateArrayToDate(input);

  // If it's a string (like from localStorage), convert
  if (typeof input === 'string') return new Date(input);

  // fallback
  return null;
}
