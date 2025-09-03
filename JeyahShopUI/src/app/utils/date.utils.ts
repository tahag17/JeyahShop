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
