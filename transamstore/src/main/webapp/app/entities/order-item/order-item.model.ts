import { ITicket } from 'app/entities/ticket/ticket.model';
import { ITicketOrder } from 'app/entities/ticket-order/ticket-order.model';

export interface IOrderItem {
  id: number;
  quantity?: number | null;
  totalPrice?: number | null;
  ticket?: ITicket | null;
  order?: ITicketOrder | null;
}

export type NewOrderItem = Omit<IOrderItem, 'id'> & { id: null };
