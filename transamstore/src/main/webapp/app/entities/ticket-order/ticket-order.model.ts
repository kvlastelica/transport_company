import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';
import { OrderStatus } from 'app/entities/enumerations/order-status.model';

export interface ITicketOrder {
  id: number;
  placedDate?: dayjs.Dayjs | null;
  status?: keyof typeof OrderStatus | null;
  code?: string | null;
  invoiceId?: number | null;
  customer?: ICustomer | null;
}

export type NewTicketOrder = Omit<ITicketOrder, 'id'> & { id: null };
