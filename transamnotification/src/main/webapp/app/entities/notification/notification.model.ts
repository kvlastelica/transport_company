import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { NotificationType } from 'app/entities/enumerations/notification-type.model';

export interface INotification {
  id: string;
  date?: dayjs.Dayjs | null;
  details?: string | null;
  sentDate?: dayjs.Dayjs | null;
  format?: keyof typeof NotificationType | null;
  userId?: number | null;
  productId?: number | null;
  user?: Pick<IUser, 'id'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
