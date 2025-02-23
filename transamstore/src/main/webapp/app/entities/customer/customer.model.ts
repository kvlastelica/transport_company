import { IUser } from 'app/entities/user/user.model';
import { CustomerType } from 'app/entities/enumerations/customer-type.model';

export interface ICustomer {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  type?: keyof typeof CustomerType | null;
  email?: string | null;
  phone?: string | null;
  addressLine1?: string | null;
  addressLine2?: string | null;
  city?: string | null;
  country?: string | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
