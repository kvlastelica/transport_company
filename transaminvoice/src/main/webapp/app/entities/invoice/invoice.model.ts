import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { InvoiceStatus } from 'app/entities/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

export interface IInvoice {
  id: string;
  code?: string | null;
  ticketOrderCode?: string | null;
  date?: dayjs.Dayjs | null;
  details?: string | null;
  status?: keyof typeof InvoiceStatus | null;
  paymentMethod?: keyof typeof PaymentMethod | null;
  paymentDate?: dayjs.Dayjs | null;
  paymentAmount?: number | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewInvoice = Omit<IInvoice, 'id'> & { id: null };
