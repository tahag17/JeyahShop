import { BackendUser } from '../shared/models/backend-user.model';
import { User } from '../shared/models/user.model';
import { convertToDate } from './date.utils';

// export function mapBackendUserToUser(backend: BackendUser): User {
//   return {
//     ...backend,
//     creationDate: convertDateArrayToDate(backend.creationDate),
//     lastModifiedDate: convertDateArrayToDate(backend.lastModifiedDate),
//   };
// }
export function mapBackendUserToUser(backend: BackendUser): User {
  return {
    ...backend,
    creationDate: convertToDate(backend.creationDate),
    lastModifiedDate: convertToDate(backend.lastModifiedDate),
  };
}
